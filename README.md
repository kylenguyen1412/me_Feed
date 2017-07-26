# meFeed
This project is my attempt to develop a simple app feed page in android which is widely used in social network platform, including image compression algorhitm and MVC pattern. 

The Feed app can be used to upload to database with 3 types of contents: text messages, images and videos and the backend service I used is Firebase API. Image file type will be reduce to a smaller size ( Max Height is 816, or Max width of 612 pixel) before sending to the server. I attempted to compress video file type aswell but I failed it, but I will do it again in near future. 

First screen of the application is used to displayed all of the content from Firebase, including images and videos. I used RecyclerView to display all of the data. The second screen is used to add content from the phone to the FIrebase database. To understand my code, you can read my comment which is appeared in every class and in every method.

# License 
This project is licensed under Unlicense license. This license does not require you to take the license with you to your project.
