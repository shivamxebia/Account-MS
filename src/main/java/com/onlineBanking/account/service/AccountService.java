package com.onlineBanking.account.service;

import java.util.List;

import com.onlineBanking.account.entity.Account;
import com.onlineBanking.account.exception.AccountApplicationException;

public interface AccountService {
	String updateBalance(Long userId, Long amount, String transactionType) throws AccountApplicationException;

	String createAccount(Long userId, String accountType) throws AccountApplicationException;

	List<Account> getAllAccounts();
}
