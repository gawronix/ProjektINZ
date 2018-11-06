function tplawesome(e,t){res=e;for(var n=0;n<t.length;n++){res=res.replace(/\{\{(.*?)\}\}/g,function(e,r){return t[n][r]})}return res}

$(function() {
    $("form").on("submit", function(e) {
       e.preventDefault();

       var request = gapi.client.youtube.search.list({
            part: "snippet",
            type: "channel",
            q: encodeURIComponent($("#search").val()).replace(/%20/g, "+"),
            maxResults: 10,
            order: "viewCount"
       });

       request.execute(function(response) {
          var results = response.result;
          $("#results tr.item").html("");
          $.each(results.items, function(index, item) {
            var request2 = gapi.client.youtube.channels.list({
              'part': 'snippet,contentDetails,statistics',
              'id': item.id.channelId
            }).then(function(response) {
              var channel = response.result.items[0];
              //console.log(channel);
              $.get("item.html", function(data) {
                  $("#results").append(tplawesome(data, [{"title":item.snippet.title, "url":item.snippet.thumbnails.default.url, "sub":channel.statistics.subscriberCount, "count":channel.statistics.viewCount, "film":channel.statistics.videoCount}]));
              });
            });
          });
          resetVideoHeight();
       });
    });

    $(window).on("resize", resetVideoHeight);
});

function resetVideoHeight() {
    $(".video").css("height", $("#results").width() * 9/16);
}

function init() {
    gapi.client.setApiKey("AIzaSyCOSupGCh2TPgM6Gu0JR2RvN5l1GtziOxw");
    gapi.client.load("youtube", "v3", function() {
        // yt api is ready
    });
}
