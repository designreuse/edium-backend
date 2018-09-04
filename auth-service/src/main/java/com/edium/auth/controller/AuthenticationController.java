package com.edium.auth.controller;

import com.edium.auth.config.CustomTokenService;
import com.edium.auth.config.SmtpMailSender;
import com.edium.auth.exceptions.InvalidRequestException;
import com.edium.auth.model.Account;
import com.edium.auth.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;

/**
 * This controller is responsible to manage the authentication
 * system. Login - Register - Forgot password - Account Confirmation
 */
@RestController
public class AuthenticationController extends BaseController{

    @Autowired
    protected AuthenticationManager authenticationManager;

    @Autowired
    private AccountService accountService;

    @Autowired
    private SmtpMailSender smtpMailSender;

    @Autowired
    private DefaultTokenServices tokenServices;

    @RequestMapping(value="/auth/sample", method= RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Account> sampleGet(HttpServletResponse response){
        return new ResponseEntity<Account>(accountService.findByUsername("papidakos"), HttpStatus.CREATED);
    }

    @RequestMapping(value="/auth/sample", method= RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Account> sample(HttpServletResponse response){
        return new ResponseEntity<Account>(accountService.findByUsername("papidakos"), HttpStatus.CREATED);
    }

    @RequestMapping("/auth/user")
    public Principal user(Principal user) {
        return user;
    }

    /**
     * Create a new user account
     * @param account user account
     * @return created account as json
     */
    @RequestMapping(value="/auth/register", method= RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Account> register(@Valid @RequestBody Account account, BindingResult errors){

        // Check if account is unique
        if(errors.hasErrors()){
            throw new InvalidRequestException("Username already exists", errors);
        }

        Account createdAccount = accountService.createNewAccount(account);
        return new ResponseEntity<Account>(createdAccount, HttpStatus.CREATED);
    }

    @RequestMapping(value="/auth/forgot-password", method=RequestMethod.GET)
    public ResponseEntity<String> forgotPassword() throws MessagingException {
        String response = "{success: true}";
        smtpMailSender.send("hiencnpm@gmail.com", "Password forgot", "Forgot password url");
        return new ResponseEntity<String>(response, HttpStatus.OK);
    }

    @RequestMapping(value="/auth/logout", method=RequestMethod.GET)
    public ResponseEntity<?> logOut(HttpServletRequest request, OAuth2Authentication auth) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.contains("Bearer")) {
            String tokenId = authorization.substring("Bearer".length() + 1);

            tokenServices.revokeToken(tokenId);
            if (tokenServices instanceof CustomTokenService) {
                ((CustomTokenService) tokenServices).revokeAllToken(auth.getPrincipal().toString());
            }
        }
        return ResponseEntity.ok().build();
    }

}
