package com.example.application.backend.ui;

import com.example.application.backend.domain.Country;
import com.example.application.backend.service.CoronaService;
import com.example.application.backend.service.GeoIpService;
import com.example.application.component.CoronaChart;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinRequest;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;

@Slf4j
@CssImport(value = "./styles/views/corona/charts.css", themeFor = "vaadin-chart", include = "vaadin-chart-default-theme")
@Route(value = "coronadashboard", layout = MainView.class)
public class CoronaDashboard extends Div implements HasUrlParameter<String>, AfterNavigationObserver, HasDynamicTitle {

    @Autowired
    private CoronaService coronaService;

    @Autowired
    private GeoIpService geoIpService;

    @Autowired
    private ApplicationContext applicationContext;

    private CoronaChart coronaChart;

    private String selectedCountryCode;

    @PostConstruct
    public void init() {
        HorizontalLayout title = new HorizontalLayout(new H1("Covid-19 Dashboard"));
        title.addClassName("title");

        coronaChart = applicationContext.getBean(CoronaChart.class, createCountrySelectorListener());
        add(coronaChart);
    }

    private HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ComboBox<Country>, Country>> createCountrySelectorListener() {
        return event -> {
            if (event.isFromClient()) {
                UI.getCurrent().navigate(CoronaDashboard.class, event.getValue().getIsoCode());
            }
        };
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String isoCode) {
        String ip = getIP();
        selectedCountryCode = isoCode;
        if (selectedCountryCode == null || selectedCountryCode.isEmpty()) {
            selectedCountryCode = geoIpService.getIsoCode(ip);
        }

        try {
            log.info(String.format("IP - ISO code: %s - %s", ip, isoCode));
            coronaChart.setCountry(coronaService.getById(selectedCountryCode));
        } catch (FeignException e) {
            log.info("Cannot find ISO code: " + isoCode);
            coronaChart.setCountry(coronaService.getById(GeoIpService.WORLD_ISO_CODE));
            Notification.show("CountryDTO not found. Showing global data.", 5000, Notification.Position.MIDDLE);

        } catch (Exception e) {
            log.error("Error fetching data", e);
            Notification.show("Error fetching data.", 5000, Notification.Position.MIDDLE);
        }
    }

    private String getIP() {
        String ip;

        if ((ip = VaadinRequest.getCurrent().getHeader("X-Forwarded-For")) == null) {
            if ((ip = VaadinRequest.getCurrent().getHeader("Via")) == null) {
                ip = VaadinRequest.getCurrent().getRemoteHost();
            }
        }

        return ip;
    }

    @Override
    public String getPageTitle() {
        return "Covid Dashboard - " + coronaChart.getCurrentCountry().getName();
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        coronaChart.setCountry(coronaService.getById(selectedCountryCode));
    }
}
