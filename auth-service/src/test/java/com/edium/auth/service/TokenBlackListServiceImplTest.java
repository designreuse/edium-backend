package com.edium.auth.service;

import com.edium.auth.model.TokenBlackList;
import com.edium.auth.repository.TokenBlackListRepo;
import com.edium.library.exception.ResourceNotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
public class TokenBlackListServiceImplTest {

    private TokenBlackListRepo tokenBlackListRepo = Mockito.mock(TokenBlackListRepo.class);

    private TokenBlackListService tokenBlackListService = new TokenBlackListServiceImpl(tokenBlackListRepo);

    @Test(expected = ResourceNotFoundException.class)
    public void whenIsBlackListed_withJtiNotFound_thenException() {
        try {
            // setup
            String jti = "test";

            // when
            Mockito.when(tokenBlackListRepo.findByJti(jti)).thenReturn(Optional.empty());

            // then
            tokenBlackListService.isBlackListed(jti);
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "Token");
            Assert.assertEquals(ex.getFieldName(), "jti");
            throw ex;
        }
    }

    @Test
    public void whenIsBlackListed_withJtiFoundAndInBlacklist_thenReturn() {
        // setup
        TokenBlackList tokenBlackList = new TokenBlackList("test", "test", 0L);
        tokenBlackList.setBlackListed(true);

        // when
        Mockito.when(tokenBlackListRepo.findByJti(tokenBlackList.getJti())).thenReturn(Optional.of(tokenBlackList));

        // then
        Assert.assertTrue(tokenBlackListService.isBlackListed(tokenBlackList.getJti()));
    }

    @Test
    public void whenIsBlackListed_withJtiFoundAndNotInBlacklist_thenReturn() {
        // setup
        TokenBlackList tokenBlackList = new TokenBlackList("test", "test", 0L);
        tokenBlackList.setBlackListed(false);

        // when
        Mockito.when(tokenBlackListRepo.findByJti(tokenBlackList.getJti())).thenReturn(Optional.of(tokenBlackList));

        // then
        Assert.assertFalse(tokenBlackListService.isBlackListed(tokenBlackList.getJti()));
    }

    @Test
    public void whenExistJti_withJtiNotFound_thenReturnFalse() {
        // setup
        String jti = "test";

        // when
        Mockito.when(tokenBlackListRepo.findByJti(jti)).thenReturn(Optional.empty());

        Assert.assertFalse(tokenBlackListService.existJti(jti));
    }

    @Test
    public void whenExistJti_withJtiFound_thenReturnTrue() {
        // setup
        String jti = "test";

        // when
        Mockito.when(tokenBlackListRepo.findByJti(jti)).thenReturn(Optional.of(new TokenBlackList()));

        Assert.assertTrue(tokenBlackListService.existJti(jti));
    }

    @Test(expected = Exception.class)
    public void whenAddAllToBlackList_withException_thenException() {
        try {
            // setup
            String username = "test";

            // when
            Mockito.doAnswer(invocationOnMock -> {
                throw new Exception("exception");
            }).when(tokenBlackListRepo).setBlackListByUser(username);

            // then
            tokenBlackListService.addAllToBlackList(username);
        } catch (Exception ex) {
            Assert.assertEquals(ex.getMessage(), "exception");
            throw ex;
        }
    }

    @Test
    public void whenAddAllToBlackList_withNoException_thenOk() {
        // setup
        String username = "test";

        // when
        Mockito.doNothing().when(tokenBlackListRepo).setBlackListByUser(username);

        // then
        tokenBlackListService.addAllToBlackList(username);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenAddToBlackList_withJtiNotFound_thenException() {
        try {
            // setup
            String jti = "test";

            // when
            Mockito.when(tokenBlackListRepo.findByJti(jti)).thenReturn(Optional.empty());

            // then
            tokenBlackListService.addToBlackList(jti);
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "Token");
            Assert.assertEquals(ex.getFieldName(), "jti");
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenAddToBlackList_withJtiFoundAndException_thenException() {
        try {
            // setup
            String jti = "test";

            // when
            Mockito.when(tokenBlackListRepo.findByJti(jti)).thenReturn(Optional.of(new TokenBlackList()));
            Mockito.doAnswer(invocationOnMock -> {
                throw new Exception("exception");
            }).when(tokenBlackListRepo).save(Mockito.any());

            // then
            tokenBlackListService.addToBlackList(jti);
        } catch (Exception ex) {
            Assert.assertEquals(ex.getMessage(), "exception");
            throw ex;
        }
    }

    @Test
    public void whenAddToBlackList_withJtiFound_thenOk() {
        // setup
        String jti = "test";

        // when
        Mockito.when(tokenBlackListRepo.findByJti(jti)).thenReturn(Optional.of(new TokenBlackList()));
        Mockito.doReturn(new TokenBlackList()).when(tokenBlackListRepo).save(Mockito.any());

        // then
        tokenBlackListService.addToBlackList(jti);
    }

    @Test(expected = Exception.class)
    public void whenAddToEnabledList_withSetBlackListByUserException_thenException() {
        try {
            // setup
            TokenBlackList tokenBlackList = new TokenBlackList("test", "test", 0L);

            // when
            Mockito.doAnswer(invocationOnMock -> {
                throw new Exception("exception");
            }).when(tokenBlackListRepo).setBlackListByUser(tokenBlackList.getUsername());

            // then
            tokenBlackListService.addToEnabledList(tokenBlackList.getUsername(), tokenBlackList.getJti(), tokenBlackList.getExpires());
        } catch (Exception ex) {
            Assert.assertEquals(ex.getMessage(), "exception");
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenAddToEnabledList_withSaveException_thenException() {
        try {
            // setup
            TokenBlackList tokenBlackList = new TokenBlackList("test", "test", 0L);

            // when
            Mockito.doNothing().when(tokenBlackListRepo).setBlackListByUser(tokenBlackList.getUsername());
            Mockito.doAnswer(invocationOnMock -> {
                throw new Exception("exception");
            }).when(tokenBlackListRepo).save(Mockito.any());

            // then
            tokenBlackListService.addToEnabledList(tokenBlackList.getUsername(), tokenBlackList.getJti(), tokenBlackList.getExpires());
        } catch (Exception ex) {
            Assert.assertEquals(ex.getMessage(), "exception");
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenAddToEnabledList_withDeleteAllByUsernameAndExpiresBeforeException_thenException() {
        try {
            // setup
            TokenBlackList tokenBlackList = new TokenBlackList("test", "test", 0L);

            // when
            Mockito.doNothing().when(tokenBlackListRepo).setBlackListByUser(tokenBlackList.getUsername());
            Mockito.doReturn(new TokenBlackList()).when(tokenBlackListRepo).save(Mockito.any());
            Mockito.doAnswer(invocationOnMock -> {
                throw new Exception("exception");
            }).when(tokenBlackListRepo).deleteAllByUsernameAndExpiresBefore(Mockito.anyString(), Mockito.anyLong());

            // then
            tokenBlackListService.addToEnabledList(tokenBlackList.getUsername(), tokenBlackList.getJti(), tokenBlackList.getExpires());
        } catch (Exception ex) {
            Assert.assertEquals(ex.getMessage(), "exception");
            throw ex;
        }
    }

    @Test
    public void whenAddToEnabledList_thenOk() {
        // setup
        TokenBlackList tokenBlackList = new TokenBlackList("test", "test", 0L);

        // when
        Mockito.doNothing().when(tokenBlackListRepo).setBlackListByUser(tokenBlackList.getUsername());
        Mockito.doReturn(new TokenBlackList()).when(tokenBlackListRepo).save(Mockito.any());
        Mockito.doNothing().when(tokenBlackListRepo).deleteAllByUsernameAndExpiresBefore(Mockito.anyString(), Mockito.anyLong());

        // then
        tokenBlackListService.addToEnabledList(tokenBlackList.getUsername(), tokenBlackList.getJti(), tokenBlackList.getExpires());
    }
}
