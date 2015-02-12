var page = {
    id: 'index.jag',
    partial: 'layout-grid',
    js: [],
    css: [],
    blocks: ['left', 'right'],
    context: {
        left: [
            {
                id: '1',
                partial: 'widget-tab',
                js: [],
                css: [],
                blocks: [
                    []
                ],
                context: [
                    {
                        name: 'Users',
                        data: [
                            {
                                partial: 'widget-list'
                            }
                        ]
                    },
                    {
                        name: 'Issues',
                        data: [
                            {
                                partial: 'widget-list'
                            }
                        ]
                    }
                ]
            }
        ]
    }
};


var page1 = {
    _: {
        id: 'index.jag',
        partial: 'layout-grid',
        js: [],
        css: [],
        blocks: ['left', 'right']
    },
    left: [
        {
            id: '1',
            partial: 'widget-tab',
            js: [],
            css: [],
            blocks: [
                []
            ],
            context: [
                {
                    name: 'Users',
                    data: [
                        {
                            partial: 'widget-list'
                        }
                    ]
                },
                {
                    name: 'Issues',
                    data: [
                        {
                            partial: 'widget-list'
                        }
                    ]
                }
            ]
        }
    ]
};