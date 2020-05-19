package com.example.application.views.masterdetail;

import com.example.application.backend.domain.Country;
import com.example.application.backend.service.CoronaService;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Route(value = "overview", layout = MainView.class)
@PageTitle("overview")
@CssImport("./styles/views/masterdetail/master-detail-view.css")
@Slf4j
public class MasterDetailView extends Div implements AfterNavigationObserver {

    private Grid<Country> grid;

    private ListDataProvider<Country> dataProvider;

    @Autowired
    private CoronaService coronaService;

    @PostConstruct
    public void init() {
        setId("master-detail-view");
        grid = new Grid<>();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setColumnReorderingAllowed(true);
        grid.setVerticalScrollingEnabled(true);
        grid.getColumns().forEach(col -> col.setSortable(true));
        grid.setHeightFull();
        grid.addColumn(Country::getName).setHeader("Country");
        grid.addColumn(Country::getPopulation).setHeader("Population");
        grid.addColumn(Country::getTotalCases).setHeader("Total cases")
                .setClassNameGenerator(item -> {
                   if (item.getTotalCases() > 10000) {
                        return "red";
                   }
                   return null;
                });
        grid.addColumn(Country::getTotalRecovered).setHeader("Total recovered");
        grid.addColumn(Country::getTotalDeaths).setHeader("Total deaths");
        HorizontalLayout searchFilterSection = createSearchFilter();
        add(searchFilterSection, grid);
    }

    private HorizontalLayout createSearchFilter() {
        HorizontalLayout toReturn = new HorizontalLayout();
        ComboBox<Country> searchSelector = new ComboBox<>("Country");
        searchSelector.setItems(coronaService.findAll());
        searchSelector.setItemLabelGenerator(Country::getName);
        searchSelector.setWidth("400px");
        searchSelector.addValueChangeListener(event -> {
           if (event.isFromClient()) {
               dataProvider.clearFilters();
               final Country country = event.getValue();
               if (country != null) {
                   dataProvider.addFilter(item -> item.getName().equals(country.getName()));
               }
           }
        });
        toReturn.add(searchSelector);
        return toReturn;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        dataProvider = new ListDataProvider<>(coronaService.findAll());
        grid.setDataProvider(dataProvider);
    }

}
