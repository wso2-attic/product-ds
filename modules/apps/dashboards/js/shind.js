(function () {
    ues.page($("#wrapper"), {
        "title": "My Dashboard",
        "layout": {
            "name": "grid_1",
            "description": "This is a sample grid",
            "thumbnail": "https://localhost:9443/dashboards/layouts/layout-1/index.jpg",
            "url": "https://localhost:9443/dashboards/assets/layouts/layout-1/index.hbs"
        },
        "content": {
            "a": [
                {
                    "id": "9zt9k74854",
                    "name": "g1",
                    "type": "gadget",
                    "thumbnail": "https://localhost:9443/dashboards/widgets/usa-map/index.png",
                    "options": {
                        "username": {
                            "type": "string",
                            "description": "Username to be used for service invocation"
                        }
                    },
                    "data": {
                        "url": "https://localhost:9443/dashboards/tests/g1.xml"
                    },
                    "description": "Allows to view and select US states",
                    "notify": {
                        "select": {
                            "type": "address",
                            "description": "This notifies selected state"
                        },
                        "cancel": {
                            "type": "boolean",
                            "description": "This notifies cancellation of state selection"
                        }
                    }
                }
            ],
            "b": [
                {
                    "id": "rujw5jwm3i",
                    "name": "g2",
                    "type": "gadget",
                    "thumbnail": "https://localhost:9443/dashboards/assets/gadgets/usa-business-revenue/index.png",
                    "options": {
                        "username": {
                            "type": "string",
                            "description": "Username to be used for service invocation"
                        }
                    },
                    "data": {
                        "url": "https://localhost:9443/dashboards/tests/g2.xml"
                    },
                    "description": "Allows to view revenue by companies in US",
                    "listen": {
                        "state-selected": {
                            "type": "address",
                            "description": "Used to filter based on state",
                            "on": [{
                                "event": "client-country",
                                "from": "pujw6jwm3t"
                            }, {
                                "event": "user-country",
                                "from": "pujw6jwm3t"
                            }, {
                                "event": "select",
                                "from": "9zt9k74854"
                            }]
                        }
                    },
                    "notify": {
                        "select": {
                            "type": "string",
                            "description": "This notifies selected company"
                        },
                        "cancel": {
                            "type": "boolean",
                            "description": "This notifies cancellation of company selection"
                        }
                    }
                }
            ],
            "c": [
                {
                    "id": "pujw6jwm3t",
                    "name": "g3",
                    "type": "widget",
                    "thumbnail": "https://localhost:9443/dashboards/assets/gadgets/usa-business-revenue/index.png",
                    "options": {
                        "username": {
                            "type": "string",
                            "description": "Username to be used for service invocation"
                        }
                    },
                    "data": {
                        "url": "https://localhost:9443/dashboards/tests/g2.xml"
                    },
                    "description": "Allows to view revenue by companies in US",
                    "listen": {
                        "state": {
                            "type": "address",
                            "description": "Used to filter based on state",
                            "on": [{
                                "event": "select",
                                "from": "9zt9k74854"
                            }]
                        }
                    },
                    "notify": {
                        "user-country": {
                            "type": "country-code",
                            "description": "This notifies selected country"
                        }
                    }
                }
            ]
        }
    }, function () {
        console.log("page rendered");
    });
}());