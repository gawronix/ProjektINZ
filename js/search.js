function tplawesome(e,t){res=e;for(var n=0;n<t.length;n++){res=res.replace(/\{\{(.*?)\}\}/g,function(e,r){return t[n][r]})}return res}

$(function() {
    $("form").on("submit", function(e) {
       e.preventDefault();
        var value = $("#search").val();
      //  console.log("cos");
        if (value.length<2)
        {
            alert("Wprowadzać poprawne dane(wymagane co najmniej dwa znaki)!");
            return;
        };

       var request = gapi.client.youtube.search.list({
            part: "snippet",
            type: "channel",
            q: encodeURIComponent(value).replace(/%20/g, "+"),
            maxResults: 50,
            order: "viewCount"
       });

       request.execute(function(response) {

          var results = response.result;
          console.log(results);
          $("#results #resultsTableBody tr.item").html("");
          var arry = results.items.filter(
              function (valueEl) {
                return true;
                // return (valueEl.snippet.title.includes(value));
              }
          );
          $.each(arry, function(index, item) {
            var request2 = gapi.client.youtube.channels.list({
              'part': 'snippet,contentDetails,statistics',
              'id': item.id.channelId
            }).then(function(response) {
                //console.log(response);
              var x = "<img src=" + item.snippet.thumbnails.default.url + " />";
              var channel = response.result.items[0];
              var newRow = [x, item.snippet.title, channel.statistics.viewCount, channel.statistics.videoCount, channel.statistics.subscriberCount]
              $("#results").DataTable().row.add( newRow ).draw();
              //console.log(channel);
              // $.get("item.html", function(data) {
              //
              //     //$("#resultsTableBody").append(tplawesome(data, [{"title":item.snippet.title, "url":item.snippet.thumbnails.default.url, "sub":channel.statistics.subscriberCount, "count":channel.statistics.viewCount, "film":channel.statistics.videoCount}]));
              // });
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
