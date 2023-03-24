# Foursquare Linker

## How to Build
This project uses [Foursquare Places API](https://location.foursquare.com/developer/reference/places-api-overview).
An API key is required to build this project.
Add/Update the `local.properties` file to include the api key
```
fsqApiKey="<YOUR_FSQ_API_KEY>"
```

## Architecture
This app follows the [recommended Android app architecture](https://developer.android.com/topic/architecture#recommended-app-arch)
The project is divided into layers:
* UI Layer
  * `ViewModel` - view state and serves as glue between UI and Data layers
  * `Jetpack Compose` - declarative UI toolkit
* Data Layer
  * `DataSource` - wraps APIs/SDK and maps models to an API-agnostic format
  * `Repository` - wraps multiple `DataSources`, implement business rules and serves as glue between Data and UI layers

## Known issues / to be fixed
* AutoComplete text field is not yet in Compose. The AutoComplete implementation used in this app is a little buggy that the text suggestion sometimes cover the text field
* Although errors are handled in the data layer, errors are not propagated to the UI layer and are not communicated to the user
* Foursquare API sessions are not used