package com.project.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.project.shared.entities.GreetingResponse;

class GreetingDialogBox {

  interface Binder extends UiBinder<DialogBox, GreetingDialogBox> {
  }

  private static Binder uiBinder = GWT.create(Binder.class);

  @UiField
  DialogBox dialogBox;
  @UiField
  Label textToServerLabel;
  @UiField
  HTML serverResponseLabel;
  @UiField
  Button closeButton;

  public GreetingDialogBox(GreetingResponse greetingResponse) {
    uiBinder.createAndBindUi(this);

    serverResponseLabel
    .setHTML(new SafeHtmlBuilder().appendEscaped("Hello, " + greetingResponse.getUserRef().get().getName() + "!")
        .appendHtmlConstant("<br>This is your greeting message nº" + (greetingResponse.getCount() + 1) + ".")
        .appendHtmlConstant("<br><br>I am running ").appendEscaped(greetingResponse.getServerInfo())
        .appendHtmlConstant(".<br><br>It looks like you are using:<br>")
        .appendEscaped(greetingResponse.getUserAgent()).toSafeHtml());
    closeButton.setFocus(true);

    closeButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        dialogBox.hide();
      }
    });
  }

  public DialogBox asDialogBox() {
    return dialogBox;
  }

  public void center() {
    dialogBox.center();
  }
}