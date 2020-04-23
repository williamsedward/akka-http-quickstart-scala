package com.example

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import com.example.BuildingRegistry._
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout

//#import-json-formats
//#Building-routes-class
class BuildingRoutes(buildingRegistry: ActorRef[BuildingRegistry.Command])(implicit val system: ActorSystem[_]) {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))

  def getBuildings(): Future[Buildings] =
    buildingRegistry.ask(GetBuildings)
  def getBuilding(name: String): Future[GetBuildingResponse] =
    buildingRegistry.ask(GetBuilding(name, _))
  def createBuilding(Building: Building): Future[BuildingActionPerformed] =
    buildingRegistry.ask(CreateBuilding(Building, _))
  def deleteBuilding(name: String): Future[BuildingActionPerformed] =
    buildingRegistry.ask(DeleteBuilding(name, _))

  //#all-routes
  val buildingRoutes: Route =
  pathPrefix("buildings") {
    concat(
      //#buildings-get-delete
      pathEnd {
        concat(
          get {
            complete(getBuildings())
          },
          post {
            entity(as[Building]) { Building =>
              onSuccess(createBuilding(Building)) { performed =>
                complete((StatusCodes.Created, performed))
              }
            }
          })
      },
      //#buildings-get-delete
      //#buildings-get-post
      path(Segment) { name =>
        concat(
          get {
            //#retrieve-Building-info
            rejectEmptyResponse {
              onSuccess(getBuilding(name)) { response =>
                complete(response.maybeBuilding)
              }
            }
            //#retrieve-Building-info
          },
          delete {
            //#buildings-delete-logic
            onSuccess(deleteBuilding(name)) { performed =>
              complete((StatusCodes.OK, performed))
            }
            //#buildings-delete-logic
          })
      })
    //#buildings-get-delete
  }
  //#all-routes
}
