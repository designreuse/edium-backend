package com.edium.auth.repository;

import com.edium.auth.model.TokenBlackList;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE, connection = EmbeddedDatabaseConnection.NONE)
public class TokenBlackListRepoTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TokenBlackListRepo tokenBlackListRepo;

    @Test(expected = Exception.class)
    public void whenSaveTokenBlackList_withJtiNull_thenException() {
        try {
            entityManager.persistAndFlush(new TokenBlackList(null, "test", 10L));
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("ids for this class must be manually assigned before calling save()"));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveTokenBlackList_withJtiDuplicate_thenException() {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());

            entityManager.persist(new TokenBlackList(timestamp, "test", 10L));
            entityManager.persistAndFlush(new TokenBlackList(timestamp, "test2", 10L));
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("A different object with the same identifier value was already"));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveTokenBlackList_withUsernameNull_thenException() {
        try {
            entityManager.persistAndFlush(new TokenBlackList("test" + System.currentTimeMillis(), null, 10L));
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("must not be blank"));
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("propertyPath=username"));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveTokenBlackList_withExpiresNull_thenException() {
        try {
            entityManager.persistAndFlush(new TokenBlackList("test" + System.currentTimeMillis(), "test", null));
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("must not be null"));
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("propertyPath=expires"));
            throw ex;
        }
    }

    @Test
    public void whenFindByJti_withJtiNotFound_thenReturn() {
       Optional<TokenBlackList> tokenBlackList = tokenBlackListRepo.findByJti(String.valueOf(System.currentTimeMillis()));

       Assert.assertFalse(tokenBlackList.isPresent());
    }

    @Test
    public void whenFindByJti_withJtiFound_thenReturn() {
        TokenBlackList tokenBlackList = entityManager.persistAndFlush(new TokenBlackList("test" + System.currentTimeMillis(), "test", 10L));

        Optional<TokenBlackList> optionalTokenBlackList = tokenBlackListRepo.findByJti(tokenBlackList.getJti());

        Assert.assertTrue(optionalTokenBlackList.isPresent());
        Assert.assertEquals(optionalTokenBlackList.get(), tokenBlackList);
    }

    @Test
    public void whenDeleteAllByUsernameAndExpiresBefore_thenOk() {
        entityManager.persist(new TokenBlackList("test" + System.currentTimeMillis(), "test", 10L));
        entityManager.persist(new TokenBlackList("test1" + System.currentTimeMillis(), "test", 20L));
        TokenBlackList tokenBlackList2 = entityManager.persist(new TokenBlackList("test2" + System.currentTimeMillis(), "test", 30L));
        TokenBlackList tokenBlackList3 = entityManager.persist(new TokenBlackList("test3" + System.currentTimeMillis(), "test2", 30L));

        tokenBlackListRepo.deleteAllByUsernameAndExpiresBefore("test", 30L);

        List<TokenBlackList> blackLists = tokenBlackListRepo.findAll();
        Assert.assertTrue(blackLists.size() == 2);
        Assert.assertTrue(blackLists.stream().anyMatch(tokenBlackList4 -> tokenBlackList4.getJti().equals(tokenBlackList2.getJti())));
        Assert.assertTrue(blackLists.stream().anyMatch(tokenBlackList4 -> tokenBlackList4.getJti().equals(tokenBlackList3.getJti())));
    }

    @Test
    public void whenSetBlackListByUser_thenOk() {
        TokenBlackList tokenBlackList = entityManager.persist(new TokenBlackList("test" + System.currentTimeMillis(), "test", 10L));
        TokenBlackList tokenBlackList1 = entityManager.persist(new TokenBlackList("test1" + System.currentTimeMillis(), "test1", 10L));

        tokenBlackListRepo.setBlackListByUser(tokenBlackList.getUsername());

        TokenBlackList tokenBlackListNew = entityManager.find(TokenBlackList.class, tokenBlackList.getJti());
        entityManager.refresh(tokenBlackListNew);
        Assert.assertTrue(tokenBlackListNew.isBlackListed());

        TokenBlackList tokenBlackListNew1 = entityManager.find(TokenBlackList.class, tokenBlackList1.getJti());
        entityManager.refresh(tokenBlackListNew1);
        Assert.assertFalse(tokenBlackListNew1.isBlackListed());
    }

}
