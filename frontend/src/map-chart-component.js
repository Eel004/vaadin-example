import ReactDOM from "react-dom";
import retargetEvents from "./react-retarget-events-shadow-dom";
import React from "react";
import MapChart from "./MapChart/MapChart";

class MapChartComponent extends HTMLElement {
    constructor() {
        super();
        this.mountPoint = document.createElement('div');
        const shadowRoot = this.attachShadow({ mode: 'open' });
        shadowRoot.appendChild(this.mountPoint);

        ReactDOM.render(<MapChart />, this.mountPoint);
        retargetEvents(shadowRoot);
    }
}

window.customElements.define('map-chart-component', MapChartComponent);