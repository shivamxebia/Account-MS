package com.onlineBanking.account.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.onlineBanking.account.dao.AccountRepository;
import com.onlineBanking.account.entity.Account;
import com.onlineBanking.account.exception.AccountApplicationException;
import com.onlineBanking.account.request.CardRequestDto;
import com.onlineBanking.account.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService{
	
	@Autowired
	private AccountRepository accountRepository;
	
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${onlineBanking.card.url}")
    private String cardServiceEndpointUrl;



	public Long generateAccountNumberUtil(){
	    Long accountNumber;
	    do {
	        accountNumber = (long) (Math.random() * 9000000000L) + 1000000000L; 
	    } while (accountRepository.existsByAccountNo(accountNumber)); 
		return accountNumber;
	}
	
	@Override
	public String createAccount(Long userId, String accountType) throws AccountApplicationException {
		// TODO Auto-generated method stub
		
		System.out.println("userID inside acc-ms : "+userId);
		if(accountRepository.findByUserId(userId)!=null) {
			throw new AccountApplicationException(HttpStatus.CONFLICT,"User Account already exists. Please choose a different userId");
		}
		Account account = new Account();
		account.setAccountType(accountType);
		account.setUserId(userId);
		account.setBalance(0);
		Long accountNumber = generateAccountNumberUtil();
		
		account.setAccountNo(accountNumber);
		
		
		String createCardUrl = "http://localhost:8082/api/v1/card"; 
		System.out.println("URL CARD : "+createCardUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        
     // Create request payload
        CardRequestDto requestPayload = new CardRequestDto();
        requestPayload.setUserId(userId);
        System.out.println(requestPayload.getUserId());
        requestPayload.setAccountType(accountType);

        HttpEntity<CardRequestDto> requestEntity = new HttpEntity<>(requestPayload, headers);

        // Send POST request to the card service
        ResponseEntity<String> responseEntity =restTemplate.exchange(createCardUrl, HttpMethod.POST, requestEntity, String.class);
		
		if(responseEntity.getStatusCode()!= HttpStatus.OK) {
			throw new AccountApplicationException(HttpStatus.CONFLICT,"Failed Card Generation");
		}
		
		accountRepository.save(account);
		return "Account created successfully for user " + userId + " with account type " + accountType;
	}

	@Override
	public List<Account> getAllAccounts() {
		// TODO Auto-generated method stub
		
		return accountRepository.findAll();
	}

	@Override
	public String updateBalance(Long userId, Long amount, String transactionType) throws AccountApplicationException {
		// TODO Auto-generated method stub
		Account account = accountRepository.findByUserId(userId);
		if(account==null) {
			throw new AccountApplicationException(HttpStatus.NOT_FOUND, "Account doesn't exist with userId"+userId);
		}
		
		//Transaction Type = "DEBITED" or "CREDITED"
		
		if(transactionType=="DEBITED") {
			account.setBalance(account.getBalance()-amount);
		}else {
			account.setBalance(account.getBalance()+amount);
		}
		accountRepository.save(account);
		
		return "Account Balance Updated Successfully!!";
	}

}
