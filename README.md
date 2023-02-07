# rum-shadow-boiler

Boilerplate generator for webapps using rum and shadow-cljs.

## Overview

**Note:** This project is new and might contain several bugs and functionality can still have breaking changes!

This project aims to provide a simple boilerplate generator for new Clojurescript projects. A new project created with this boilerplate includes the following components:

- [babashka](https://github.com/babashka/babashka) to automate and control the project
- [shadow-cljs](https://github.com/thheller/shadow-cljs) for easy Clojurescript compilation
- [rum](https://github.com/tonsky/rum) as a ReactJS wrapper
- [tailwind](https://tailwindcss.com/) as a CSS framework
- [storybook](https://storybook.js.org/) for easy Component development and quick testing
- [cypress](https://github.com/cypress-io/cypress) for Component Testing
- [clci](https://github.com/ClockworksIO/clci) for simple workflows
- [mkdocs](https://www.mkdocs.org/) with the [material theme](https://squidfunk.github.io/mkdocs-material/) to build a beautiful documentation


## Usage

To create a new project checkout this repository and run the _new project_ Babashka task:

```sh
bb new project --name "fancy" \
	--path "./fancy/" \
	-d "An example app to build a fancy PWA."  \
	-w "https://my-fancy-example.com"  \
	-r "htps://github.com/fancy/fancy.git"  \
	-o "Fancy Inc."  \
	-N "fancy"  \
	-a "John Doe <johndoe@my-fancy-example.com>"  \
	-l "Apache-2.0"
```