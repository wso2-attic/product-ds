/**
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
var log = new Log();

try {
    var dashboard = require("/modules/dashboards.js");
    var config = require("/configs/sample.json");
} catch (Exception) {
    log.error("Required File (/modules/dashboards.js or /configs/sample.json) is missing");
}

if (config.isSampleOn) {
    var DASHBOARD = {
        "id": "demographics-of-usa",
        "title": "Demographics of USA",
        "description": "Statistical details about Demographics of USA.",
        "permissions": {"viewers": ["Internal/everyone"], "editors": ["Internal/everyone"]},
        "pages": [{
            "id": "landing",
            "title": "Welcome",
            "layout": {
                "id": "layout-2",
                "title": "Right Grid",
                "description": "This is a sample grid",
                "thumbnail": "store://layout/layout-2/index.png",
                "url": "store://layout/layout-2/index.hbs",
                "content": "<div class=\"container\">\n\n   <div class=\"row\">\n        <div id=\"a\" class=\"col-md-4 ues-component-box\"></div>\n        <div id=\"b\" class=\"col-md-4 ues-component-box\"></div>\n        <div id=\"c\" class=\"col-md-4 ues-component-box\"></div>\n    </div>\n    <div class=\"row\">\n        <div id=\"d\" class=\"col-md-10 ues-component-box\"></div>\n        <div id=\"e\" class=\"col-md-2 ues-component-box\"></div>\n    </div>\n    <div class=\"row\">\n        <div id=\"f\" class=\"col-md-4 ues-component-box\"></div>\n        <div id=\"g\" class=\"col-md-4 ues-component-box\"></div>\n \t<div id=\"h\" class=\"col-md-4 ues-component-box\"></div>\n    </div>\n\n</div>\n"
            },
            "isanon": false,
            "content": {
                "default": {
                    "d": [{
                        "id": "dzdig2mle9rizfr",
                        "content": {
                            "id": "usa-states",
                            "title": "Population Density Of USA",
                            "type": "gadget",
                            "thumbnail": "store://gadget/usa-population-density/images/index.png",
                            "options": {},
                            "data": {"url": "store://gadget/usa-population-density/index.xml"},
                            "notify": {"state": {"type": "state", "description": "This notifies the selected state"}},
                            "listen": {
                                "age": {
                                    "type": "number",
                                    "description": "Listen to the age channel",
                                    "on": [{"from": "bim7z762yg1zto6r", "event": "age"}]
                                },
                                "ethnicity": {
                                    "type": "ethnicity",
                                    "description": "Listen to the ethnicity channel",
                                    "on": [{"from": "btg82n4yrki96bt9", "event": "ethnicity"}]
                                },
                                "gender": {
                                    "type": "gender",
                                    "description": "Listen to the gender channel",
                                    "on": [{"from": "bim7z762yg1zto6r", "event": "gender"}]
                                },
                                "history": {
                                    "type": "year",
                                    "description": "Listen to the history channel",
                                    "on": [{"from": "prs8krsgrlxiggb9", "event": "history"}]
                                }
                            },
                            "styles": {
                                "height": "",
                                "borders": true,
                                "titlePosition": "left",
                                "title": "Population Density Of USA"
                            },
                            "locale_titles": {"en-US": "Population Density Of USA"},
                            "settings": {}
                        }
                    }],
                    "g": [{
                        "id": "btg82n4yrki96bt9",
                        "content": {
                            "id": "donutchart",
                            "title": "Population by Ethnicity",
                            "type": "gadget",
                            "thumbnail": "store://gadget/usa-population-by-ethnicity/images/index.png",
                            "options": {},
                            "data": {"url": "store://gadget/usa-population-by-ethnicity/index.xml"},
                            "notify": {
                                "ethnicity": {
                                    "type": "ethnicity",
                                    "description": "This notifies the selected state"
                                }
                            },
                            "listen": {
                                "age": {
                                    "type": "number",
                                    "description": "Used to filter based on state",
                                    "on": [{"from": "bim7z762yg1zto6r", "event": "age"}]
                                },
                                "state": {
                                    "type": "state",
                                    "description": "Used to filter based on state",
                                    "on": [{"from": "dzdig2mle9rizfr", "event": "state"}]
                                }
                            },
                            "styles": {
                                "height": "",
                                "borders": true,
                                "titlePosition": "left",
                                "title": "Population by Ethnicity"
                            },
                            "locale_titles": {"en-US": "Population by Ethnicity"},
                            "settings": {}
                        }
                    }],
                    "f": [{
                        "id": "bim7z762yg1zto6r",
                        "content": {
                            "id": "barchart",
                            "title": "Population by Age and Gender",
                            "type": "gadget",
                            "thumbnail": "store://gadget/usa-population-by-age-and-gender/images/index.png",
                            "options": {},
                            "data": {"url": "store://gadget/usa-population-by-age-and-gender/index.xml"},
                            "notify": {
                                "age": {"type": "number", "description": "This notifies the selected Age"},
                                "gender": {"type": "gender", "description": "This notifies the selected Gender"}
                            },
                            "listen": {
                                "state": {
                                    "type": "state",
                                    "description": "Used to listen to state channel",
                                    "on": [{"from": "dzdig2mle9rizfr", "event": "state"}]
                                }
                            },
                            "styles": {
                                "height": "",
                                "borders": true,
                                "titlePosition": "left",
                                "title": "Population by Age and Gender"
                            },
                            "locale_titles": {"en-US": "Population by Age and Gender"},
                            "settings": {}
                        }
                    }],
                    "h": [{
                        "id": "prs8krsgrlxiggb9",
                        "content": {
                            "id": "linechart",
                            "title": "Population History",
                            "type": "gadget",
                            "thumbnail": "store://gadget/usa-population-history/images/index.png",
                            "options": {},
                            "data": {"url": "store://gadget/usa-population-history/index.xml"},
                            "notify": {"history": {"type": "year", "description": "This notifies the selected year"}},
                            "listen": {
                                "state": {
                                    "type": "state",
                                    "description": "Used to filter based on state",
                                    "on": [{"from": "dzdig2mle9rizfr", "event": "state"}]
                                }
                            },
                            "styles": {
                                "height": "",
                                "borders": true,
                                "titlePosition": "left",
                                "title": "Population History"
                            },
                            "locale_titles": {"en-US": "Population History"},
                            "settings": {}
                        }
                    }]
                }, "anon": {}
            }
        }],
        "identityServerUrl": "",
        "accessTokenUrl": "",
        "apiKey": "",
        "apiSecret": "",
        "banner": {"globalBannerExists": null, "customBannerExists": null},
        "landing": "landing",
        "isanon": false,
        "isUserCustom": false
    };

    log.info("Deleting existing sample dashboard");
    dashboard.remove("demographics-of-usa");

    log.info("Creating sample dashboard");
    dashboard.create(DASHBOARD);

    // Reset the sample.json file to previous.
    var file = new File("/configs/sample.json");
    file.open("w");
    file.write('{\n\t"isSampleOn": false\n}');
    file.close();
}