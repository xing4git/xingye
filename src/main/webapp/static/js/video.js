var player = new qcVideo.Player(
    "tx_video",
    {
        "file_id":14651978969258928890,
        "app_id":1252102344,
        "auto_play":1,
        "width":640,
        "height":480
    },
    {
        "fullScreen":function(isFullScreen) {

        },
        "playStatus":function(status) {

        },
        "dragPlay":function(second) {

        }
    }
);
player.play(0);