Network Front Archive
=====================

Here's the original idea:

- poll the network front at regular intervals
- is this one different to the last poll?
	- if yes then, save in some persistant store

- then a ui that just lists the captured versions and allows display
  (something like a simple list on the left hand side)


And a simpler idea (just to capture what things looked like):

poller:

- every 5 minutes use phantomjs to capture a screenshot
- create a sensibly cropped thumbnail
- upload with consitent name to s3 bucket

displayer:

- entirely based of scanning the s3 bucket

So, if we create something a bucket naming structure like:

 - bucket/<urlpath>/2013/04/18/2013-04-18T10:15.png
 and
 - bucket/<urlpath>/2013/04/18/2013-04-18T10:15_thumb.png

Then we can just scan for the buckets to display everything.

(in fact, this logic could easily be some pages within the ophan dashboard, since it
should be really simple)