jQuery ->
  # left = 37; right = 39
  # up = 38; down = 40
  console.log "starting"

  # pick up the stuff that the server put in the page for us
  curIdx = global_curIdx
  screenShots = global_screens
  curShot = screenShots[curIdx]

  refreshDisplay = ->
    console.log "idx = #{curIdx} setting src to #{curShot.img}..."
    $("#picker-time").text(curShot.time)
    $("#screenshot").attr("src", curShot.img)

  $(document).keyup (e) ->
    console.log "Keyup: #{e.which}"
    if e.which == 39
      curIdx += 1
      curIdx = 0 if curIdx >= screenShots.length
      curShot = screenShots[curIdx]
      refreshDisplay()
    else if e.which == 37
      curIdx -= 1
      curIdx = screenShots.length - 1 if curIdx < 0
      curShot = screenShots[curIdx]
      refreshDisplay()