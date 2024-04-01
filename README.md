# Assignment 5: Crafting with Compose

An Android application that allows the user to log in to view a list of equipment and food products
retrieved from a server using Retrofit. The products are stored locally in a Room database so that
they can be displayed later if the device is offline, and a WorkManager is used to periodically load
new products. The UI is implemented using Jetpack Compose.

### GitHub Link

The GitHub link for this project is https://github.com/sam-schu/cs4520-assignment5.

### Getting Started

To run the project, simply open it in Android Studio, choose a device to emulate (this project was
tested primarily with an emulated Pixel 5), and press the Run button at the top of the window. The
UI components in the app, as well as the device's back button, can be used for navigation.

### Project Overview

This is a single-activity application with two screens. When the user first opens the app, the
login screen is displayed; they must enter the correct username and password and press the
"Login" button to move to the second screen. When the user moves to the product list screen,
products are attempted to be loaded either from a server if the device is online, or from a local
database of previously loaded products if the device is offline. (The server sometimes provides no
products or an error instead of the correct list of products.) The product list screen uses a
LazyColumn to display a scrollable list of equipment and food products. Equipment products are
shown in red, and food products are shown in yellow.

### Project Structure

The source code for the project is divided into three packages under the com.cs4520.assignment5
package: logic, model, and ui. The logic package contains the Authenticator object needed to manage
business logic for the login fragment, the view model for the product list fragment, and worker
files for the product list. The model
package contains classes needed for the Room database and for storing products into and loading
products from it. The ui package contains classes for each activity and screen in the application,
as well as the Navigation file needed for Compose navigation. The
com.cs4520.assignment5 package also includes the Api file, which enables Retrofit to load data from
the server. Images used by the project can be found in the project's res folder.