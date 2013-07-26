package com.project.client;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.entities.User;
import com.seanchenxi.gwt.storage.client.StorageExt;
import com.seanchenxi.gwt.storage.client.StorageKey;
import com.seanchenxi.gwt.storage.client.StorageKeyFactory;

public class View extends Composite {

  interface ViewUiBinder extends UiBinder<Widget, View> {
  }

  private static ViewUiBinder uiBinder = GWT.create(ViewUiBinder.class);

  @UiField
  protected TextBox nameTextBox;
  @UiField
  protected Button saveBtn;
  @UiField
  protected InlineLabel lastCreatedUserLabel;
  @UiField
  protected Button storeBtn;
  @UiField
  protected HTMLPanel databaseUsersPanel;
  @UiField
  protected Button databaseReloadBtn;
  @UiField
  protected Button databaseClearBtn;
  @UiField
  protected HTMLPanel localStorageUsersPanel;
  @UiField
  protected Button localStorageReloadBtn;
  @UiField
  protected Button localStorageClearBtn;
  private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
  private User lastCreatedUser;
  private final StorageExt localStorage = StorageExt.getLocalStorage();
  private final StorageKey<StoredUsers> key = StorageKeyFactory.objectKey("STORAGE_USERS_KEY");

  public View() {
    initWidget(uiBinder.createAndBindUi(this));
    nameTextBox.getElement().setAttribute("placeholder", "User's name");

    saveBtn.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        if (!nameTextBox.getText().trim().isEmpty()) {
          User newUser = new User();
          newUser.setName(nameTextBox.getText().trim());
          greetingService.save(newUser, new AsyncCallback<User>() {
            @Override
            public void onFailure(Throwable caught) {
              Logger.getLogger("").log(Level.SEVERE, "", caught);
            }

            @Override
            public void onSuccess(User user) {
              Logger.getLogger("").info(user.getName() + " has been saved.");
              nameTextBox.setText(null);
              setLastCreatedUser(user);
              databaseReloadUsers();
            }
          });
        } else {
          Window.alert("Please give a name");
        }
      }
    });

    storeBtn.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        if (lastCreatedUser == null) {
          Window.alert("No User has been created yet.");
        } else {
          localStore(lastCreatedUser);
          localStorageReloadUsers();
        }
      }
    });

    databaseReloadBtn.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        databaseReloadUsers();
      }
    });
    databaseReloadUsers();

    databaseClearBtn.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        greetingService.clearUsers(new AsyncCallback<Void>() {
          @Override
          public void onFailure(Throwable caught) {
            Logger.getLogger("").log(Level.SEVERE, "Database hasn't been cleared.", caught);
          }

          @Override
          public void onSuccess(Void result) {
            Logger.getLogger("").info("Database has been cleared.");
            databaseReloadUsers();
          }
        });
      }
    });

    localStorageReloadBtn.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        localStorageReloadUsers();
      }
    });
    localStorageReloadUsers();

    localStorageClearBtn.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        if (localStorage != null) {
          if (localStorage.containsKey(key)) {
            try {
              localStorage.remove(key);
              Logger.getLogger("").info("Local Storage has been cleared.");
              localStorageReloadUsers();
            } catch (Exception e) {
              Logger.getLogger("").log(Level.SEVERE, "Local Storage hasn't been cleared.", e);
            }
          }
        }
      }
    });
  }

  private void databaseReloadUsers() {
    databaseUsersPanel.clear();
    greetingService.getUsers(new AsyncCallback<List<User>>() {
      @Override
      public void onFailure(Throwable caught) {
        Logger.getLogger("").log(Level.SEVERE, "Reload Users from Database has failed.", caught);
      }

      @Override
      public void onSuccess(List<User> users) {
        Logger.getLogger("").info("Reload Users from Database has succeeded.");
        for (User user : users) {
          databaseUsersPanel.add(new HTMLPanel("li", user.getName()));
        }
      }
    });
  }

  private void initLocalStorage() {
    if (localStorage != null) {
      if (!localStorage.containsKey(key)) {
        try {
          final StoredUsers storedUsers = new StoredUsers();
          localStorage.put(key, storedUsers);
          Logger.getLogger("").info("Local Storage has been initialized.");
        } catch (Exception e) {
          Logger.getLogger("").log(Level.SEVERE, "Local Storage hasn't been initialized.", e);
        }
      }
    }
  }

  private void localStorageReloadUsers() {
    localStorageUsersPanel.clear();
    if (localStorage != null) {
      if (localStorage.containsKey(key)) {
        try {
          final StoredUsers storedUsers = localStorage.get(key);
          for (User user : storedUsers.getUsers()) {
            localStorageUsersPanel.add(new HTMLPanel("li", user.getName()));
          }
          Logger.getLogger("").info("Reload Users from Local Storage has succeeded.");
        } catch (Exception e) {
          Logger.getLogger("").log(Level.SEVERE, "Reload Users from Local Storage has failed.", e);
        }
      }
    }
  }

  private void localStore(User user) {
    if (localStorage != null) {
      initLocalStorage();
      try {
        final StoredUsers storedUsers = localStorage.get(key);
        final boolean added = storedUsers.getUsers().add(user);
        localStorage.put(key, storedUsers);
        if (added) {
          Logger.getLogger("").info(user.getName() + " has been stored in Local Storage.");
        }
      } catch (Exception e) {
        Logger.getLogger("").log(Level.SEVERE, user.getName() + " hasn't been stored in Local Storage.", e);
      }
    }
  }

  private void setLastCreatedUser(User user) {
    lastCreatedUser = user;
    lastCreatedUserLabel.setText(user.getName());
  }

}
