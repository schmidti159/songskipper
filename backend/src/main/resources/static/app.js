function formatTime(milliseconds) {
    var minutes = Math.floor(milliseconds / 1000 / 60);
    var seconds = Math.floor((milliseconds / 1000) - minutes * 60);
    seconds = (seconds < 10) ? "0" + seconds : seconds;
    return minutes+":"+seconds;
}

function createLink(content, href) {
    return '<a href='+href+' target="_blank" rel="noreferrer noopener">'+content+'</a>';
}

function trackUpdate(msg) {
    console.log(msg);
    var title = createLink(msg.trackName, msg.trackLink);

    var subtitle = msg.artistNames
        .map(
            (_, i) => createLink(msg.artistNames[i], msg.artistLinks[i]))
        .join(', ');
    subtitle += " - " + createLink(msg.albumName, msg.albumLink);
    $('#current-track-title')
        .html(title);
    $('#current-track-subtitle')
        .html(subtitle);

    var progressPercent = msg.progressMs / msg.durationMs * 100;
    progressContent = formatTime(msg.progressMs)+" / "+formatTime(msg.durationMs);
    $('#current-track-progress')
        .css('width', progressPercent+'%')
        .attr('aria-valuenow', Math.floor(msg.progressMs/1000))
        .attr('aria-valuemax', Math.floor(msg.durationMs/1000))
        .html(progressContent);

    $('img#current-track-artwork')
        .attr('src', msg.albumArtworkLink)
        .attr('alt', msg.albumName);
}
$(function() {
    var socket = new SockJS('/ws');
    var stompClient = Stomp.over(socket)
    stompClient.connect({}, function(frame) {
        console.log('Connected: '+frame);
        stompClient.subscribe('/users/queue/messages',function(message) {
            trackUpdate(JSON.parse(message.body))
        });
        // start skipping
        $.get('api/v1/skipper/start', {}, () => console.log('started skipper'));
    })();
});