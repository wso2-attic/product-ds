var url,ws;
  
window.onload = function WindowLoad(event) {
    url = 'wss://'+window.location.hostname+':'+window.location.port+'/ws-chat/server.jag';
    ws = new WebSocket(url);
  
    //event handler for the message event in the case of text frames
    ws.onopen = function() {
    console.log("web Socket onopen. ");
    };
    ws.onmessage = function(event) {
    console.log("web Socket Onmessage from Server. " + event.data);
    var reply = document.getElementById('msg-content');
    reply.innerHTML = reply.innerHTML + '<br/>' + event.data;
    };
    ws.onclose = function() {
    console.log("web Socket onclose. ");
    };
}
  
//send msg to the server
function send(){
    var msg = document.getElementById('msg');
    ws.send(msg.value);
    console.log("Client message "+msg.value);
    msg.value = '';
}

