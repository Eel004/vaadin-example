import React, { useState, useEffect, memo } from "react";
import ReactDOM from "react-dom";
import retargetEvents from 'react-shadow-dom-retarget-events';
import ReactTooltip from "react-tooltip";
import {
    ComposableMap,
    Geographies,
    Geography
} from "./react-simple-maps-modified";

const geoUrl =
    "https://raw.githubusercontent.com/zcreativelabs/react-simple-maps/master/topojson-maps/world-110m.json";

const MapChart = (props) => {
    const [hoveringCountryName, setHoveringCountryName] = useState("");
    const [hoveringCountryIsoCode, setHoveringCountryIsoCode] = useState("");
    const [selectedCountryIsoCode, setSelectedCountryIsoCode] = useState("");
    const [clientX, setClientX] = useState(0);
    const [clientY, setClientY] = useState(0);
    const [tooltipVisible, setTooltipVisible] = useState('hidden');

    const styles = {
        visibility: tooltipVisible,
        top: (clientY + 20) + 'px',
        left: (clientX + 20) + 'px',
        position: 'absolute',
        width: "120px",
        backgroundColor: "black",
        color: "#fff",
        textAlign: "center",
        borderRadius: "6px",
        padding: "5px 0",
        zIndex: "1"
    };

   useEffect(() => {
       setSelectedCountryIsoCode(props.selectedCountryIsoCode);
   }, [props])

    return (
        <>
            <span className="tooltip-span" style={styles}>{hoveringCountryName}</span>
            <ComposableMap data-tip="" projectionConfig={{ scale: 100 }} projection="geoMercator">
                <Geographies geography={geoUrl}>
                    {({ geographies }) =>
                        geographies.map(geo => (
                            <Geography
                                key={geo.rsmKey}
                                geography={geo}
                                onMouseEnter={(e) => {
                                    const { NAME, POP_EST, ISO_A2 } = geo.properties;
                                    setHoveringCountryName(`${NAME}`);
                                    setHoveringCountryIsoCode(`${ISO_A2}`);
                                    setClientX(e.clientX);
                                    setClientY(e.clientY);
                                    setTooltipVisible('visible');
                                }}
                                onMouseLeave={() => {
                                    setHoveringCountryName("");
                                    setHoveringCountryIsoCode("");
                                    setTooltipVisible('hidden');
                                }}
                                onMouseUp={() => {
                                    setSelectedCountryIsoCode(hoveringCountryIsoCode);
                                    props.countryClickedHandler(hoveringCountryIsoCode);
                                }}
                                style={{
                                    default: {
                                        fill: selectedCountryIsoCode === geo.properties['ISO_A2'] ? "#F53" : "#D6D6DA",
                                        outline: "none"
                                    },
                                    hover: {
                                        fill: "#F53",
                                        outline: "none"
                                    },
                                    pressed: {
                                        fill: "#E42",
                                        outline: "none"
                                    }
                                }}
                            />
                        ))
                    }
                </Geographies>
            </ComposableMap>
        </>
    );
};

class MapChartComponent extends HTMLElement {
    constructor() {
        super();
        this.mountPoint = document.createElement('div');
        const shadowRoot = this.attachShadow({ mode: 'open' });
        shadowRoot.appendChild(this.mountPoint);

        ReactDOM.render(this.createMapChart(), this.mountPoint);
        retargetEvents(shadowRoot);
    }

    createMapChart(selectedCountryIsoCode) {
        return (<MapChart countryClickedHandler={(newIsoCode) => {this.selectedCountryIsoCode = newIsoCode;}}
                          selectedCountryIsoCode={selectedCountryIsoCode}/>);
    }

    static get observedAttributes() {
      return ['selectedcountryisocode'];
    }

    get selectedCountryIsoCode() {
        return this.getAttribute('selectedcountryisocode');
    }

    set selectedCountryIsoCode(newIsoCode) {
        this.setAttribute('selectedcountryisocode', newIsoCode);
        ReactDOM.render(this.createMapChart(newIsoCode), this.mountPoint);
    }
}

window.customElements.define('map-chart-component', MapChartComponent);