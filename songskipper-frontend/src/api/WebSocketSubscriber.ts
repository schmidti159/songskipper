
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client'

import { currentlyPlayingMessage } from '../features/currentlyPlaying/currentlyPlayingSlice'

export default function connectWebsocket(dispatch: any) {
    const stompClient = new Client({
        webSocketFactory: function () {
            return new SockJS('http://'+document.location.host+'/ws');
        }
    });
    stompClient.onConnect = frame => {
        console.log('Connected: '+frame);
        stompClient.subscribe('/users/queue/messages', message => {
            const currentlyPlaying = JSON.parse(message.body)
            dispatch(currentlyPlayingMessage(currentlyPlaying))
        });
    };
    stompClient.onWebSocketError = error => {console.log('websocketError: '+error); console.log(error);}
    stompClient.onStompError =  frame => {
        console.log('Broker reported error: ' + frame.headers['message']);
        console.log('Additional details: ' + frame.body);
      };
    stompClient.activate();
    console.log('activated stompClient');
}
