class HelloButton extends HTMLElement {

    constructor() {
        super();

        // Create a shadow root
        const shadow = this.attachShadow({mode: 'open'});
        shadow.innerHTML = `<div style="background: yellow; padding: 20px;">
            Web component
            <br />
            <input />
            <button onclick="alert(this.getRootNode().querySelector('input').value)">Hello</button>
        </div>`;
    }

    changeBackground(color) {
        this.shadowRoot.querySelector('div').style.background = color;
    }

    getInputValue() {
        return this.shadowRoot.querySelector('input').value;
    }
}

customElements.define('hello-button', HelloButton);