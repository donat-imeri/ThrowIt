# ThrowIt
This project is part of the Mobile Programming course and it shows the usage of animation and sensor data to throw a ball upwards.  

# How to build the app
The app supports all API levels starting from the 16th and up. </br>
To build the app clone this repository by using `git clone https://github.com/donat-imeri/ThrowIt ` </br>
Open Android Studio and open the project from it</br>
Now you can run the app

# How to use the app
After starting the app you see a basketball which will be disappeared after you throw (to get a feel that the ball is not anymore in your hand) 
it by accelerating your phone. </br>
The distance will change and reflect the current distance of the ball from the ground. A sound is played as well while this is done. 
After the ball gets to its highest position it will start to fall down and an other sound is played, while this is done the distance 
will be decreased till the ball appears again which means that the ball is again in your hand. You can always change the threshold value
which is the sensitivity value of the app so that when you are just moving the phone slowly the ball is not thrown. One feature that is not 
working is the landscape mode, so you can use the app only in the portrait mode and the user can not switch to landscape. 

# Extras
* You can always turn on or off the sound by clicking the icon on the top left of the screen
* Different sounds are played when the ball is moving up or down. 
* The app saves your high score, so you can always challenge yourself! 
* The app has an icon
* The score text and the ball are drawn using a canvas so the main UI thread is not affected and it doesn't feel laggy. 




# Sensitivity
In my personal mobile phone the game seems to perform best when the ssensitivity value is around 15. When it is to small the ball is throwed in
short distances, so by increasing it to this value I get a quite real feel. The sliding window size is 20 and seems to work quite well. 


# Checklist

* [x] The git repository URL is correctly provided, such that command works: `git clone <url> `
* [x] The code is well, logically organised and structured into appropriate classes. Everything should be in a single package.
* [x] The app has been user tested with someone other than the author.
* [x] The user can go to Preferences and set the MIN_ACC value (sensitivity).
* [x] The app plays sounds on the ball highest point.
* [x] The app records the highest point reached by the ball.
* [x] Share your sensitivity constant as well as the size of the sliding window with others.
* [x] The repo is *public*, such that reviewers can access it and review it.
* [x] The repo has a README.md file, explaining how to build the app, how to use the app, which features are working, which are not working, how the code is organised, and what extras does the project have.
