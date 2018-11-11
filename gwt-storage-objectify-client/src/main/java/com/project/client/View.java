package com.project.client;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;
import org.fusesource.restygwt.client.TextCallback;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.github.nmorel.gwtjackson.remotelogging.shared.RemoteThrowable;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Longs;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.logging.client.HasWidgetsLogHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.GreetingService;
import com.project.shared.entities.GreetingResponse;
import com.project.shared.entities.User;

import de.knightsoftnet.validators.client.impl.Validation;

public class View extends Composite {

  private static final ViewUiBinder uiBinder = GWT.create(ViewUiBinder.class);
  private static final String STORAGE_USERS_KEY = "STORAGE_USERS_KEY";
  private static final Logger logger = Logger.getLogger("");
  private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
  private static final GreetingService GREETING_SERVICE = GWT.create(GreetingService.class);
  private final Storage localStorage = Storage.getLocalStorageIfSupported();
  @UiField FlowPanel loggingPanel;
  @UiField TextBox nameTextBox;
  @UiField Button sendBtn;
  @UiField InlineLabel lastCreatedUserLabel;
  @UiField Button storeBtn;
  @UiField HTMLPanel databaseUsersPanel;
  @UiField Button databaseReloadBtn;
  @UiField Button databaseClearBtn;
  @UiField HTMLPanel localStorageUsersPanel;
  @UiField Button localStorageReloadBtn;
  @UiField Button localStorageClearBtn;
  @UiField Button logOnServerBtn;
  @UiField Button switchStackModeBtn;
  @UiField InlineLabel currentStackMode;
  @UiField Anchor commitId;
  @UiField Label dirty;
  private User lastCreatedUser;

  public View() {
    initWidget(uiBinder.createAndBindUi(this));
    logger.addHandler(new HasWidgetsLogHandler(loggingPanel) {
      @Override
      public void publish(LogRecord record) {
        if (!isLoggable(record)) {
          return;
        }
        Formatter formatter = getFormatter();
        String msg = formatter.format(record);
        HTMLPanel pre = new HTMLPanel("pre", msg);
        pre.getElement().getStyle().setWhiteSpace(Style.WhiteSpace.PRE_WRAP);
        loggingPanel.add(pre);
      }
    });

    nameTextBox.getElement().setAttribute("placeholder", "User's name");

    sendBtn.addClickHandler(event -> processUsername());
    nameTextBox.addKeyPressHandler(event -> {
      if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
        processUsername();
      }
    });

    storeBtn.addClickHandler(event -> {
      if (lastCreatedUser == null) {
        logger.warning("No User has been created yet.");
      } else {
        localStore(lastCreatedUser);
        localStorageReloadUsers();
      }
    });

    databaseReloadBtn.addClickHandler(event -> databaseReloadUsers());
    databaseReloadUsers();

    databaseClearBtn.addClickHandler(event -> REST.withCallback(new MethodCallback<Void>() {
      @Override
      public void onFailure(Method method, Throwable throwable) {
        logger.log(Level.SEVERE, "Database hasn't been cleared.", throwable);
      }

      @Override
      public void onSuccess(Method method, Void response) {
        logger.info("Database has been cleared.");
        databaseReloadUsers();
      }
    }).call(GREETING_SERVICE).clearUsers());

    localStorageReloadBtn.addClickHandler(event -> localStorageReloadUsers());
    localStorageReloadUsers();

    localStorageClearBtn.addClickHandler(event -> {
      if (localStorage != null) {
        if (localStorage.getItem(STORAGE_USERS_KEY) != null) {
          try {
            localStorage.removeItem(STORAGE_USERS_KEY);
            logger.info("Local Storage has been cleared.");
            localStorageReloadUsers();
          } catch (Exception e) {
            logger.log(Level.SEVERE, "Local Storage hasn't been cleared.", e);
          }
        }
      }
    });

    logOnServerBtn.addClickHandler(event -> {
      Throwable throwable = new Throwable();
      REST.withCallback(new TextCallback() {
        @Override
        public void onFailure(Method method, Throwable throwable) {
          logger.log(Level.SEVERE, "Cannot log on Server.", throwable);
        }

        @Override
        public void onSuccess(Method method, String response) {
          logger.log(Level.INFO, "Client StackTrace", throwable);
          logger.log(Level.INFO, "Server/Deobfuscated StackTrace\n" + response);
        }
      }).call(App.DEOBFUSCATOR_SERVICE).deobfuscate(new RemoteThrowable(throwable), "INFO", GWT.getPermutationStrongName());
    });

    String stackMode = System.getProperty("compiler.stackMode");
    currentStackMode.setText(stackMode);

    switchStackModeBtn.addClickHandler(event -> {
      UrlBuilder urlBuilder = new UrlBuilder();
      urlBuilder.setProtocol(Window.Location.getProtocol());
      urlBuilder.setHost(Window.Location.getHost());
      urlBuilder.setParameter("compiler.stackMode", Objects.equals("native", stackMode) ? "emulated" : "native");
      Window.Location.replace(urlBuilder.buildString());
    });

    Git git = GitObjectMapper.INSTANCE.read(Resources.INSTANCE.git().getText());
    commitId.setText(git.gitCommitIdAbbrev);
    commitId.setHref("https://github.com/freddyboucher/gwt-storage-objectify/commit/" + git.gitCommitIdAbbrev);
    dirty.setVisible(git.gitDirty);
    dirty.setText("Dirty: " + git.gitDirty);
  }

  private void databaseReloadUsers() {
    databaseUsersPanel.clear();
    REST.withCallback(new MethodCallback<List<User>>() {
      @Override
      public void onFailure(Method method, Throwable throwable) {
        logger.log(Level.SEVERE, "Reload Users from Database has failed.", throwable);
      }

      @Override
      public void onSuccess(Method method, List<User> users) {
        logger.info("Reload Users from Database has succeeded.");
        users.forEach(user -> databaseUsersPanel.add(new HTMLPanel("li", user.getName())));
      }
    }).call(GREETING_SERVICE).getUsers();
  }

  private void enabledNameFields(boolean enabled) {
    sendBtn.setEnabled(enabled);
    nameTextBox.setEnabled(enabled);
  }

  private void initLocalStorage() {
    if (localStorage != null) {
      if (localStorage.getItem(STORAGE_USERS_KEY) == null) {
        try {
          localStorage.setItem(STORAGE_USERS_KEY, UsersObjectMapper.INSTANCE.write(Collections.emptySet()));
          logger.info("Local Storage has been initialized.");
        } catch (Exception e) {
          logger.log(Level.SEVERE, "Local Storage hasn't been initialized.", e);
        }
      }
    }
  }

  private void localStorageReloadUsers() {
    localStorageUsersPanel.clear();
    if (localStorage != null) {
      String item = localStorage.getItem(STORAGE_USERS_KEY);
      if (item != null) {
        try {
          UsersObjectMapper.INSTANCE.read(item).forEach(user -> localStorageUsersPanel.add(new HTMLPanel("li", user.getName())));
          logger.info("Reload Users from Local Storage has succeeded.");
        } catch (Exception e) {
          logger.log(Level.SEVERE, "Reload Users from Local Storage has failed.", e);
          localStorage.removeItem(STORAGE_USERS_KEY);
          logger.log(Level.INFO, "Local Storage has been cleared.", e);
        }
      }
    }
  }

  private void localStore(User user) {
    if (localStorage != null) {
      initLocalStorage();
      try {
        Set<User> users = UsersObjectMapper.INSTANCE.read(localStorage.getItem(STORAGE_USERS_KEY));
        boolean added = users.add(user);
        localStorage.setItem(STORAGE_USERS_KEY, UsersObjectMapper.INSTANCE.write(users));
        if (added) {
          logger.info(user.getName() + " has been stored in Local Storage.");
        }
      } catch (Exception e) {
        logger.log(Level.SEVERE, user.getName() + " hasn't been stored in Local Storage.", e);
      }
    }
  }

  private void processUsername() {
    if (sendBtn.isEnabled() && nameTextBox.isEnabled()) {
      Set<ConstraintViolation<User>> violations = VALIDATOR.validateValue(User.class, "name", nameTextBox.getText().trim(), Default.class);
      if (violations.isEmpty()) {
        enabledNameFields(false);
        REST.withCallback(new MethodCallback<GreetingResponse>() {
          @Override
          public void onFailure(Method method, Throwable throwable) {
            logger.log(Level.SEVERE, "greetServer has thrown an Exception.", throwable);
            enabledNameFields(true);
          }

          @Override
          public void onSuccess(Method method, GreetingResponse greetingResponse) {
            User user = greetingResponse.getUserRef().get();
            if (greetingResponse.getCount() == 0) {
              logger.info("A new User name:" + user.getName() + " id:" + user.getId() + " has been saved.");
            }
            // Guava GWT works well in front-end code
            String idBase32Encoded = BaseEncoding.base32().encode(Longs.toByteArray(user.getId()));
            logger.info("User id:" + user.getId() + " base32 encoded:" + idBase32Encoded);

            GreetingDialogBox greetingDialogBox = new GreetingDialogBox(greetingResponse);
            greetingDialogBox.asDialogBox().addCloseHandler(event -> nameTextBox.setFocus(true));
            greetingDialogBox.center();

            nameTextBox.setText(null);
            setLastCreatedUser(user);
            databaseReloadUsers();
            enabledNameFields(true);
          }
        }).call(GREETING_SERVICE).greetServer(nameTextBox.getText().trim());
      } else {
        violations.forEach(constraintViolation -> logger.warning(constraintViolation.getMessage()));
      }
    }
  }

  private void setLastCreatedUser(User user) {
    lastCreatedUser = user;
    lastCreatedUserLabel.setText(user.getName());
  }

  interface Resources extends ClientBundle {
    Resources INSTANCE = GWT.create(Resources.class);

    @Source("git.properties")
    TextResource git();
  }

  public interface GitObjectMapper extends ObjectMapper<Git> {
    GitObjectMapper INSTANCE = GWT.create(GitObjectMapper.class);
  }

  public interface UsersObjectMapper extends ObjectMapper<Set<User>> {
    UsersObjectMapper INSTANCE = GWT.create(UsersObjectMapper.class);
  }

  interface ViewUiBinder extends UiBinder<Widget, View> {}
}
