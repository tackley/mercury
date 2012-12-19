Network Front Archive
=====================

Here's the idea:

- poll the network front at regular intervals
- is this one different to the last poll?
	- if yes then, save in some persistant store

- then a ui that just lists the captured versions and allows display
  (something like a simple list on the left hand side)


Update

Well that was the original idea. Some more thoughts:

- what I really want to know is "where has this item of content been promoted"

- e.g. for url X:

  - 9:45-9:50 Sport front -> Auto position 5
  - 9:45-10:00 UK Network front -> Main trailblock -> position 7 sublink 2
  - 10:30-10:35 UK network front -> Special trail -> position 1
  - etc

- how to do this:
  - every minute, poll all the fronts and store
     time + url + page + component + position
  - then given a url
     - get all the "promotions"
     - aggregate the contiguous minute entries for the same component + position
     - return


- detecting simple "position" is easy - just do which url in the list of the component

- sublinks is more tricky - can we identify that it is a sublink -
    yes: if one of its parents is "sublinks" then it's a sublink