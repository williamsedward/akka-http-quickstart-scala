package com.example

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import scala.collection.immutable

final case class Building(name: String, levels: Int, countryOfResidence: String)
final case class Buildings(buildings: immutable.Seq[Building])

object BuildingRegistry {
  sealed trait Command
  final case class GetBuildings(replyTo: ActorRef[Buildings]) extends Command
  final case class CreateBuilding(building: Building, replyTo: ActorRef[BuildingActionPerformed]) extends Command
  final case class GetBuilding(name: String, replyTo: ActorRef[GetBuildingResponse]) extends Command
  final case class DeleteBuilding(name: String, replyTo: ActorRef[BuildingActionPerformed]) extends Command

  final case class GetBuildingResponse(maybeBuilding: Option[Building])
  final case class BuildingActionPerformed(description: String)

  def apply(): Behavior[Command] = registry(Set.empty)

  private def registry(buildings: Set[Building]): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetBuildings(replyTo) =>
        replyTo ! Buildings(buildings.toSeq)
        Behaviors.same
      case CreateBuilding(building, replyTo) =>
        replyTo ! BuildingActionPerformed(s"Building ${building.name} created.")
        registry(buildings + building)
      case GetBuilding(name, replyTo) =>
        replyTo ! GetBuildingResponse(buildings.find(_.name == name))
        Behaviors.same
      case DeleteBuilding(name, replyTo) =>
        replyTo ! BuildingActionPerformed(s"Building $name deleted.")
        registry(buildings.filterNot(_.name == name))
    }
}
