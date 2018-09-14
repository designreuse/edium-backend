package com.edium.auth.controller;

import com.edium.auth.security.CustomTokenService;
import com.edium.auth.config.SmtpMailSender;
import com.edium.auth.exceptions.InvalidRequestException;
import com.edium.auth.security.RestFB;
import com.edium.auth.security.TokenUtils;
import com.edium.library.model.core.User;
import com.edium.auth.service.UserService;
import com.edium.library.model.UserPrincipal;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;

/**
 * This controller is responsible to manage the authentication
 * system. Login - Register - Forgot password - User Confirmation
 */
@RestController
public class AuthenticationController extends BaseController{

    @Autowired
    protected AuthenticationManager authenticationManager;

    @Autowired
    private UserService accountService;

    //@Autowired
    private SmtpMailSender smtpMailSender;

    @Autowired
    private DefaultTokenServices tokenServices;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RestFB restFB;

    @Autowired
    private TokenUtils tokenUtils;

    @RequestMapping("/auth/user")
    public Principal user(Principal user) {
        return user;
    }

    @RequestMapping("/auth/login/facebook")
    public ResponseEntity loginFacebook(HttpServletRequest request) throws Exception {
        String code = request.getParameter("code");

        if (code == null || code.isEmpty()) {
            throw new Exception("Facebook Code is empty");
        }

        String clientId = request.getParameter("clientId");
        if (clientId == null || clientId.isEmpty()) {
            throw new Exception("clientId is empty");
        }

        String accessToken = restFB.getToken(code);

        com.restfb.types.User user = restFB.getUserInfo(accessToken);

        User localUser = accountService.findByUsernameOrEmail(user.getEmail());
        if (localUser == null) {
            localUser = new User();
            localUser.setPasswordDefault(true);
            localUser.setPassword(passwordEncoder.encode(RandomStringUtils.randomAlphabetic(8)));
        }
        restFB.buildUser(user, localUser);
        accountService.saveOrUpdate(localUser);

        UserPrincipal principal = UserPrincipal.create(localUser);

        return new ResponseEntity<>(tokenUtils.getToken(principal, clientId), HttpStatus.OK);
    }

    /**
     * Create a new core account
     * @param account core account
     * @return created account as json
     */
    @RequestMapping(value="/auth/register", method= RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> register(@Valid @RequestBody User account, BindingResult errors){

        // Check if account is unique
        if(errors.hasErrors()){
            throw new InvalidRequestException("Username already exists", errors);
        }

        User createdAccount = accountService.createNewAccount(account);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    @RequestMapping(value="/auth/forgot-password", method=RequestMethod.GET)
    public ResponseEntity<String> forgotPassword() throws MessagingException {
        String response = "{success: true}";
        smtpMailSender.send("hiencnpm@gmail.com", "Password forgot", "Forgot password url");
        return new ResponseEntity<>(response, HttpStatus.OK);
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
