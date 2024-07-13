package com.onlineBanking.account.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onlineBanking.account.entity.Account;
import com.onlineBanking.account.exception.AccountApplicationException;
import com.onlineBanking.account.request.CreateAccountRequestDto;
import com.onlineBanking.account.request.UpdateBalanceRequestDto;
import com.onlineBanking.account.service.AccountService;


@RestController
@RequestMapping("/api/v1/")
public class AccountController {
	
	private final AccountService accountService;
	
	@Autowired	
	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}


	@PostMapping("account")
	ResponseEntity<String> createAccount(@RequestBody CreateAccountRequestDto request) throws AccountApplicationException{
		System.out.println("userID inside acc-ms-cont : "+request.getUserId());
	    String response = accountService.createAccount(request.getUserId(), request.getAccountType());
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> allAccounts = accountService.getAllAccounts();
        if (allAccounts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(allAccounts);
        }
        return ResponseEntity.ok(allAccounts);
    }
	

	@PatchMapping("/update-balance")
	ResponseEntity<String> updateBalance(@RequestBody UpdateBalanceRequestDto updateBalanceRequestDto) throws AccountApplicationException{
		String response = accountService.updateBalance(updateBalanceRequestDto.getUserId(),updateBalanceRequestDto.getAmount(),updateBalanceRequestDto.getTransactionType());
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
