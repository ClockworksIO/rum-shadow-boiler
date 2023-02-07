const { defineConfig } = require("cypress");

module.exports = defineConfig({

  component: {
    supportFile: "cypress/support/components.js",
    specPattern: "resources/cy-component/compiled/**/*_cypress.js",
    devServer: {
      framework: "react",
      bundler: "webpack",
    },
  },
});
