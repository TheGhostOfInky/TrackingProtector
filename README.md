# Tracking Protector

This repository hosts the source code for a simple Android app to remove trackers from Reddit share URLs.

## Background

Soon after its brutal and sudden crackdown on 3rd party apps in June of 2023, Reddit's official android app started sharing comment and post links in a rather unorthodox format that obscures the actual comment and post IDs behind a unique, per-share tracking ID that allows reddit to know exactly who you send a given link to.

In the process these links also break many older, but still functional, 3rd party clients that are not equipped to handle these new obscured links.

## How this app works

This app accepts obscure reddit share links and emits clean links that contain no trackers and are supported by most 3rd party clients, regardless of age.

## How do I know if my link is an obscured link

Obscured reddit links follow the format:
```
    https://www.reddit.com/r/<subreddit>/s/<share-id>
```

Whereas conventional links look something like the following:
```
    https://www.reddit.com/r/<subreddit>/comments/<comment-id>
```

You can easily indentify tracking links by the `/s/` after the subreddit name.