package com.edium.auth.service;

import com.edium.auth.model.Account;

import java.util.Collection;

public interface AccountService {

    Collection<Account> findAll();

    Account findByUsername(String userename);

    Account createNewAccount(Account account);


}
