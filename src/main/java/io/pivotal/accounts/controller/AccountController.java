package io.pivotal.accounts.controller;

import java.math.BigDecimal;
import java.util.List;

import io.pivotal.accounts.domain.Account;
import io.pivotal.accounts.domain.AccountType;
import io.pivotal.accounts.service.AccountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * REST controller for the accounts microservice. Provides the following
 * endpoints:
 * <p>
 * <ul>
 * <li>GET <code>/accounts/{id}</code> retrieves the account with given id.
 * <li>POST <code>/accounts</code> stores the account object passed in body.
 * <li>GET <code>/accounts/{id}/increaseBalance/{amount}</code> increases the
 * balance of the account with given id by amount.
 * <li>GET <code>/accounts/{id}/decreaseBalance/{amount}</code> decreases the
 * balance of the account with given id by amount.
 * </ul>
 * <p>
 * 
 * @author David Ferreira Pinto
 * @author Maxim Avezbakiev
 *
 */
@RestController
public class AccountController {

	private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

	/**
	 * The service to delegate calls to.
	 */
	@Autowired
	private AccountService service;

	/**
	 * REST call to retrieve the account with the given id.
	 * 
	 * @param id
	 *            The id of the account to retrieve the account for.
	 * @return The account object if found.
	 */
	@RequestMapping(value = "/accounts/{id}", method = RequestMethod.GET)
	public ResponseEntity<Account> find(@PathVariable("id") final Integer id) {

		logger.info("AccountController.find: id=" + id);

		Account accountResponse = this.service.findAccount(id);
		return new ResponseEntity<Account>(accountResponse, getNoCacheHeaders(), HttpStatus.OK);

	}

	@RequestMapping(value = "/accounts", method = RequestMethod.GET)
	public ResponseEntity<List<Account>> findAccounts(@RequestParam(value = "name") final String id,@RequestParam(value = "type", required = false) final String type) {

		logger.info("AccountController.findAccount: id=" + id);
		if (type == null) {
			List<Account> accountResponse = this.service.findAccounts(id);
			return new ResponseEntity<List<Account>>(accountResponse, getNoCacheHeaders(), HttpStatus.OK);
		} else {
			List<Account> accountResponse = this.service.findAccountsByType(id, AccountType.valueOf(type));
			return new ResponseEntity<List<Account>>(accountResponse, getNoCacheHeaders(), HttpStatus.OK);
		}
	}
	

	/**
	 * REST call to save the account provided in the request body.
	 * 
	 * @param accountRequest
	 *            The account to save.
	 * @param builder
	 * @return
	 */
	@RequestMapping(value = "/accounts", method = RequestMethod.POST)
	public ResponseEntity<String> save(@RequestBody Account accountRequest, UriComponentsBuilder builder) {

		logger.debug("AccountController.save: userId=" + accountRequest.getUserid());

		Integer accountProfileId = this.service.saveAccount(accountRequest);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(builder.path("/account/{id}").buildAndExpand(accountProfileId).toUri());
		return new ResponseEntity<String>(responseHeaders, HttpStatus.CREATED);
	}
	
	//TODO: consolidate increaseBalance and decreaseBalance into a POST for a transaction.

	//TODO: this should be a POST
	/**
	 * REST call to decrease the balance in the account. Decreases the balance
	 * of the account if the new balance is not lower than zero. Returns HTTP OK
	 * and the new balance if the decrease was successful, or HTTP
	 * EXPECTATION_FAILED if the new balance would be negative and the
	 * old/current balance.
	 * 
	 * @param accountId
	 *            The id of the account.
	 * @param amount
	 *            The amount to decrease the balance by.
	 * @return The new balance of the account with HTTP OK.
	 */
	@RequestMapping(value = "/accounts/{accountId}/decreaseBalance/{amount}", method = RequestMethod.GET)
	public ResponseEntity<Double> decreaseBalance(@PathVariable("accountId") final Integer accountId, @PathVariable("amount") final double amount) {

		logger.debug("AccountController.decreaseBalance: id='" + accountId + "', amount='" + amount + "'");

		Account accountResponse = this.service.findAccount(accountId);

		BigDecimal currentBalance = accountResponse.getBalance();

		BigDecimal newBalance = currentBalance.subtract(new BigDecimal(amount));

		if (newBalance.compareTo(BigDecimal.ZERO) >= 0) {
			accountResponse.setBalance(newBalance);
			this.service.saveAccount(accountResponse);
			return new ResponseEntity<Double>(accountResponse.getBalance().doubleValue(), getNoCacheHeaders(), HttpStatus.OK);

		} else {
			// no sufficient founds available
			return new ResponseEntity<Double>(accountResponse.getBalance().doubleValue(), getNoCacheHeaders(), HttpStatus.EXPECTATION_FAILED);
		}

	}

	//TODO: this should be a POST
	/**
	 * REST call to increase the balance in the account. Increases the balance
	 * of the account if the amount is not negative. Returns HTTP OK and the new
	 * balance if the increase was successful, or HTTP EXPECTATION_FAILED if the
	 * amount given is negative.
	 * 
	 * @param accountId
	 *            The id of the account.
	 * @param amount
	 *            The amount to increase the balance by.
	 * @return The new balance of the account with HTTP OK.
	 */
	@RequestMapping(value = "/accounts/{accountId}/increaseBalance/{amount}", method = RequestMethod.GET)
	public ResponseEntity<Double> increaseBalance(@PathVariable("accountId") final Integer accountId, @PathVariable("amount") final double amount) {

		logger.debug("AccountController.increaseBalance: id='" + accountId + "', amount='" + amount + "'");

		Account accountResponse = this.service.findAccount(accountId);

		BigDecimal currentBalance = accountResponse.getBalance();

		logger.debug("AccountController.increaseBalance: current balance='" + currentBalance + "'.");

		if (amount > 0) {

			BigDecimal newBalance = currentBalance.add(new BigDecimal(amount));
			logger.debug("AccountController.increaseBalance: new balance='" + newBalance + "'.");

			accountResponse.setBalance(newBalance);
			this.service.saveAccount(accountResponse);
			return new ResponseEntity<Double>(accountResponse.getBalance().doubleValue(), getNoCacheHeaders(), HttpStatus.OK);

		} else {
			// amount can not be negative for increaseBalance, please use
			// decreaseBalance
			return new ResponseEntity<Double>(accountResponse.getBalance().doubleValue(), getNoCacheHeaders(), HttpStatus.EXPECTATION_FAILED);
		}

	}

	private HttpHeaders getNoCacheHeaders() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Cache-Control", "no-cache");
		return responseHeaders;
	}
}
