# YoutubeUploader

YoutubeUploader is a webapp project. This project allows you to upload video files to this server (optimal in your local area network). The web application transforms the video file to optimal youtube settings (based on ffmpeg) and attach an optional intor and outro to that video. After finishing the video is uploaded as a private video to YouTube with all special descriptions and tags you add in the web interface. At least the uploader publish the video to public after a given timestamp.  Beside the upload to youtube the applicatin also add some notifications to twitter, google and facebook.

# Installation

- install a web application server like tomcat
- checkout this repository, run mvn clean install and copy the war file to your webapp server
- install mysql 
- add a youtubeuploader.properties file to your home directory and modify the required properties
- install ffmpeg 
- startup the server
 
# Notes

- the application is currently under development and draft
- all video files will be stored on the disk and need about two or three times of the normal file size disk space. This meeans when you upload a 1 GB file about 2 or 3 GB are required for transformations. 
- You should note that for video transformations it is faster when you have a graphic card in the server which is used by ffmpeg.
