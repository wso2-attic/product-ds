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
}catch(Exception){
    log.error("Required File (/modules/dashboards.js or /configs/sample.json) is missing");
}

if(config.isSampleOn) {
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
                        "id": "9v35hu5u2e9daemi",
                        "content": {
                            "id": "usa-states",
                            "title": "Population Density Of USA",
                            "type": "gadget",
                            "thumbnail": "store://gadget/usa-population-density/images/index.png",
                            "options": {},
                            "data": {"url": "store://gadget/usa-population-density/index.xml"},
                            "notify": {"state": {"type": "address", "description": "This notifies the selected state"}},
                            "listen": {
                                "age": {
                                    "type": "address",
                                    "description": "Listen to the age channel",
                                    "on": [{"from": "7khbx7d06yrs5rk9", "event": "age"}]
                                },
                                "ethnicity": {
                                    "type": "address",
                                    "description": "Listen to the ethnicity channel",
                                    "on": [{"from": "9ek38q6mg9cnmi", "event": "ethnicity"}]
                                },
                                "gender": {
                                    "type": "address",
                                    "description": "Listen to the gender channel",
                                    "on": [{"from": "7khbx7d06yrs5rk9", "event": "gender"}]
                                },
                                "history": {
                                    "type": "address",
                                    "description": "Listen to the history channel",
                                    "on": [{"from": "a6btuca9q2u4bo6r", "event": "history"}]
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
                    "f": [{
                        "id": "7khbx7d06yrs5rk9",
                        "content": {
                            "id": "barchart",
                            "title": "USA Population by Age and Gender",
                            "type": "gadget",
                            "thumbnail": "store://gadget/usa-population-by-age-and-gender/images/index.png",
                            "options": {},
                            "data": {"url": "store://gadget/usa-population-by-age-and-gender/index.xml"},
                            "notify": {
                                "age": {"type": "address", "description": "This notifies the selected Age"},
                                "gender": {"type": "address", "description": "This notifies the selected Gender"}
                            },
                            "listen": {
                                "state": {
                                    "type": "address",
                                    "description": "Used to listen to state channel",
                                    "on": [{"from": "9v35hu5u2e9daemi", "event": "state"}]
                                }
                            },
                            "styles": {
                                "height": "",
                                "borders": true,
                                "titlePosition": "left",
                                "title": "USA Population by Age and Gender"
                            },
                            "locale_titles": {"en-US": "USA Population by Age and Gender"},
                            "settings": {}
                        }
                    }],
                    "g": [{
                        "id": "9ek38q6mg9cnmi",
                        "content": {
                            "id": "donutchart",
                            "title": "USA Population by Ethnicity",
                            "type": "gadget",
                            "thumbnail": "store://gadget/usa-population-by-ethnicity/images/index.png",
                            "options": {},
                            "data": {"url": "store://gadget/usa-population-by-ethnicity/index.xml"},
                            "notify": {
                                "ethnicity": {
                                    "type": "address",
                                    "description": "This notifies the selected state"
                                }
                            },
                            "listen": {
                                "age": {
                                    "type": "address",
                                    "description": "Used to filter based on state",
                                    "on": [{"from": "7khbx7d06yrs5rk9", "event": "age"}]
                                },
                                "state": {
                                    "type": "address",
                                    "description": "Used to filter based on state",
                                    "on": [{"from": "9v35hu5u2e9daemi", "event": "state"}]
                                }
                            },
                            "styles": {
                                "height": "",
                                "borders": true,
                                "titlePosition": "left",
                                "title": "USA Population by Ethnicity"
                            },
                            "locale_titles": {"en-US": "USA Population by Ethnicity"},
                            "settings": {}
                        }
                    }],
                    "h": [{
                        "id": "a6btuca9q2u4bo6r",
                        "content": {
                            "id": "linechart",
                            "title": "USA Population History",
                            "type": "gadget",
                            "thumbnail": "store://gadget/usa-population-history/images/index.png",
                            "options": {},
                            "data": {"url": "store://gadget/usa-population-history/index.xml"},
                            "notify": {
                                "history": {
                                    "type": "address",
                                    "description": "This notifies the selected year"
                                }
                            },
                            "listen": {
                                "state": {
                                    "type": "address",
                                    "description": "Used to filter based on state",
                                    "on": [{"from": "9v35hu5u2e9daemi", "event": "state"}]
                                }
                            },
                            "styles": {
                                "height": "",
                                "borders": true,
                                "titlePosition": "left",
                                "title": "USA Population History"
                            },
                            "locale_titles": {"en-US": "USA Population History"},
                            "settings": {}
                        }
                    }]
                }, "anon": {}
            }
        }],
        "landing": "landing",
        "isanon": false,
        "isUserCustom": false
    };

    log.info("*** Deleting existing dashboard ***");
    dashboard.remove("demographics-of-usa");

    log.info("*** Creating sample dashboard ***");
    dashboard.create(DASHBOARD);

    // Reset the sample.json file to previous.
    var file = new File("/configs/sample.json");
    file.open("w");
    file.write('{\n\t"isSampleOn": false\n}');
    file.close();
}