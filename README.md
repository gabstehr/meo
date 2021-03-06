# meo

**meo** is a **personal information manager** for recording and improving my life. It starts with a **geo-aware journal** where each entry captures your geo-location and whatever you want to note about that moment. You can use hashtags and mentions to better organize your information. You can also track your **tasks** and **habits**.

Here's how that currently looks like:

![screenshot](http://matthiasnehlsen.com/images/2018-03-08-meo-charts.png)


## Motivation

See this [blog post](http://matthiasnehlsen.com/blog/2018/03/15/introducing-meo/) for the background. More blog posts to follow.


## Components

**meo** consists of a **[Clojure](https://clojure.org/)** and **[ClojureScript](https://github.com/clojure/clojurescript)** system spanning an **Electron** application and a backend that runs on the **[JVM](https://en.wikipedia.org/wiki/Java_virtual_machine)**. There's also a **mobile companion app** written in **ClojureScript** on top of **[React Native](https://facebook.github.io/react-native/)**, see `./rn-app` in this repository. All subsystems in meo make use of the **[systems-toolbox](https://github.com/matthiasn/systems-toolbox)** for its architecture.

Here's how the app currently looks like:

![screenshot](http://matthiasnehlsen.com/images/2018-03-08-mobile.png)


## Installation

There are two install scripts, one for Ubuntu and one for the Mac. These install the dependencies required. If anything is missing, please submit a pull request. And have a look at what the script for your platform does before blindly running it, and typing in your superuser password, which is required for `apt` in the Linux version.
 
 For Mac:

    $ ./install_mac.sh
     
For Linux: 
     
    $ ./install_ubuntu.sh

Then, you need to install the JavaScript dependencies:

    $ yarn install
 
Once that is done, you need to compile the ClojureScript code into JavaScript. These need to be run with the `cljs` profile. Using this profile keeps the size of the uberjar for the JVM-based backend smaller. I usually run one or more of these in different terminals or split views:

    $ lein with-profile cljs cljsbuild auto main
    $ lein with-profile cljs cljsbuild auto renderer-dev
    $ lein with-profile cljs cljsbuild auto updater

Alternatively, you use these aliases (see project.clj);

    $ lein cljs-main-dev
    $ lein cljs-renderer-dev
    $ then cljs-updater-dev

These need to be running in separate terminals, as they watch the file system for changes in auto mode.

Next, you need to compile the SCSS files into CSS:

    $ lein sass4clj auto
 

## Usage

Once you have completed all the steps in the previous section, all you need to do is:

    $ lein run
    $ npm start


## Packaging

You can also package the application using the publish script:

    $ ./publish.sh -m beta

This script will completely build and package the meo desktop application. It will try to upload the application into an S3 bucket if credentials are in the environment. But even without valid credentials, you will still find the packaged application under `./dist/`.


## Tests

    $ lein test

or

    $ lein test2junit


[![CircleCI Build Status](https://circleci.com/gh/matthiasn/meo.svg?&style=shield)](https://circleci.com/gh/matthiasn/meo)
[![TravisCI Build Status](https://travis-ci.org/matthiasn/meo.svg?branch=master)](https://travis-ci.org/matthiasn/meo)


## How to help

Contributions and pull requests are very welcome. Please check the open issues for where help is required the most, and file issues for anything that you find.


## License

Copyright © 2016-2018 **[Matthias Nehlsen](http://www.matthiasnehlsen.com)**. Distributed under the **GNU AFFERO PUBLIC LICENSE**, Version 3. See separate LICENSE file.
