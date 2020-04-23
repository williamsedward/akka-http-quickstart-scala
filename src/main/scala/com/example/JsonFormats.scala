package com.example

import com.example.UserRegistry.UserActionPerformed
import com.example.BuildingRegistry.BuildingActionPerformed

//#json-formats
import spray.json.DefaultJsonProtocol

object JsonFormats  {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat3(User)
  implicit val usersJsonFormat = jsonFormat1(Users)

  implicit val buildingJsonFormat = jsonFormat3(Building)
  implicit val buildingsJsonFormat = jsonFormat1(Buildings)

  implicit val userActionPerformedJsonFormat = jsonFormat1(UserActionPerformed)
  implicit val buildingActionPerformedJsonFormat = jsonFormat1(BuildingActionPerformed)
}
//#json-formats
