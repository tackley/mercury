jQuery ->
  hash = document.location.hash

  if hash[0..1] == "#T"
    $("#tm-picker-time").text(hash[2..])