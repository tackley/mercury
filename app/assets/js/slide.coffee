jQuery ->
  # left = 37; right = 39
  # up = 38; down = 40

  # pick up the stuff that the server put in the page for us
  curIdx = global_curIdx
  screenShots = global_screens
  curShot = screenShots[curIdx]

  resetFlash = ->
    $("#picker-time").css("background-color", "transparent")

  refreshDisplay = ->
    #console.log "idx = #{curIdx} setting src to #{curShot.img}..."
    $("#screenshot").attr("src", curShot.img)
    $("#picker-time").text(curShot.time).css("background-color", "red")
    # some browers don't fire image loads apparently, so
    # this timeout is a fallback in that case
    setTimeout resetFlash, 5000

  $("#screenshot").load ->
    resetFlash()

  $(document).keyup (e) ->
    movementAmount = if e.altKey then 12 else 1

    if e.which == 39
      curIdx += movementAmount
      curIdx = screenShots.length-1 if curIdx >= screenShots.length
      curShot = screenShots[curIdx]
      refreshDisplay()
    else if e.which == 37
      curIdx -= movementAmount
      curIdx = 0 if curIdx < 0
      curShot = screenShots[curIdx]
      refreshDisplay()