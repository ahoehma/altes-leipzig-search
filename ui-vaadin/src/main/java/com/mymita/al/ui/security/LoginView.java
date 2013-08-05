package com.mymita.al.ui.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.gwt.thirdparty.guava.common.base.Objects;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

@Configurable
public class LoginView extends FormLayout implements View {

	private final TextField loginField = new TextField("Login");
	private final PasswordField passwordField = new PasswordField("Password");

	@Autowired
	transient AuthenticationManager authenticationManager;

	public LoginView() {
		setMargin(true);
		addComponent(loginField);
		addComponent(passwordField);
		final Button button = new Button("Login");
		button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(final ClickEvent clickEvent) {
				authenticate(Objects.firstNonNull(loginField.getValue(), ""),
						Objects.firstNonNull(passwordField.getValue(), ""));
				loginField.setValue("");
				passwordField.setValue("");
			}
		});
		addComponent(button);
	}

	private void authenticate(String username, String password) {
		final UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
				username.trim(), password);
		Authentication authResult = null;
		try {
			authResult = authenticationManager.authenticate(authRequest);
			if (authResult == null) {
				// return immediately as subclass has indicated that it hasn't
				// completed authentication
				return;
			}
		} catch (AuthenticationException failed) {
			// Authentication failed
			SecurityContextHolder.clearContext();
			return;
		}
		SecurityContextHolder.getContext().setAuthentication(authResult);
	}

	@Override
	public void enter(final ViewChangeEvent event) {

	}
}
