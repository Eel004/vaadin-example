package com.example.application.views.webcomponent;

import com.example.application.component.HelloButton;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Random;

@Route(value = "using-web-component", layout = MainView.class)
@PageTitle("Using Web Component")
public class UsingWebComponentView extends VerticalLayout {

    private static String[] helloButtonColors = {"red", "green", "blue", "yellow", "orange", "purple", "pink", "black"};

    public UsingWebComponentView() {
        getStyle().set("flex-direction", "column");

        Button changeBackgroundButton = new Button("Change background");
        Button getInputValueButton = new Button("Get input value");
        HelloButton helloButton = new HelloButton();

        changeBackgroundButton.addClickListener(e -> {
            int randomColorIndex = new Random().nextInt(helloButtonColors.length);
            helloButton.getElement().callJsFunction("changeBackground", helloButtonColors[randomColorIndex]);
        });

        getInputValueButton.addClickListener(e -> {
            PendingJavaScriptResult result = helloButton.getElement().callJsFunction("getInputValue");
            result.then(val -> Notification.show(val.asString(), 3000, Notification.Position.MIDDLE));
        });

        add(changeBackgroundButton, getInputValueButton, helloButton);
    }

}
