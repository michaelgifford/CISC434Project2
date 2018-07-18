# CISC434Project2
Android chat application made in CISC434 - Distributed Systems

READ ME

Configuring Project
------------------
1.) Unzip the zip file submitted. The front end is hosted in the folder labelled FrontEnd.
2.) Within the folder FrontEnd there is a folder named CISC434Project2. Import this folder into Android Studio as a new project.
3.) Open Android Studio. Click File > New > Import Project...
4.) A popup will appear. Select the CISC434Project2 folder as the folder to import.

Configuring Java Files
----------------------
1.) Open the files LOGINREGISTER.java, LOBBY.java, & TypeHere.java in a text editor.
2.) In LOGINREGISTER.java change the line in the first try catch of the class 
"mSocket = IO.socket("http://10.217.91.226");" TO "mSocket = IO.socket("http://yourlocalip:3000");"
3.) In LOBBY.java change the same line
"mSocket = IO.socket("http://10.217.91.226");" TO "mSocket = IO.socket("http://yourlocalip:3000");"
4.) In TypeHere.java change the variable "public static final String chat_server = "http://10.217.91.226";" to "public static final String chat_server = "http://yourlocalip:3000";"
5.) Save the aformentioned files and re-build the project

How to Run
---------- 
1.) Once everything is configured select Build > Make Module.
2.) Select Build > Make Project. 
2.) Select Run > Run 'app' to run the android device emulator and application.

REGISTER
1.) Enter desired username in top field and desired password in bottom field
2.) Click register and you'll be redirected to the chatroom lobby

LOGIN
1.) Enter account username in top field and account password in bottom field for an account you have already registered
2.) Click login and you'll be redirected to the chatroom lobby

LOBBY
1.) Scroll down to refresh the page twice and the available rooms will be displayed as a list
2.) Click on the chtroom you'd like to enter and you'll be redirected to it

CHATROOM
1.) Upon load chatroom will load preexisting messages
2.) Type anything you'd like to say and click the arrow in the bottom-right corner to send the message
3.) Direct messaging is available. It works by entering an '@' followed by a users username followed by a space and you message. Ex.) "@user hey this is a direct message" or "@user1 @user 2 @user3 hey this is a direct message". Only the users you direct message and yourself will see the message.

