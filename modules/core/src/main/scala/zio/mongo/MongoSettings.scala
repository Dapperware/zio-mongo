package zio.mongo

import zio.Config

case class MongoSettings(
  connectionString: Option[ConnectionString] = None,
  applicationName: Option[String] = None,
  readConcern: Option[ReadConcern] = None,
  writeConcern: Option[WriteConcern] = None,
  readPreference: Option[ReadPreference] = None,
  retryWrites: Option[Boolean] = None,
  retryReads: Option[Boolean] = None,
  credential: Option[Credential] = None
) {

  private[mongo] def toJava: com.mongodb.MongoClientSettings = {
    val builder = com.mongodb.MongoClientSettings.builder()
    connectionString.foreach(cs => builder.applyConnectionString(cs.asJava))
    applicationName.foreach(builder.applicationName)
    readConcern.map(_.wrapped).foreach(builder.readConcern)
    writeConcern.map(_.wrapped).foreach(builder.writeConcern)
    readPreference.map(_.wrapped).foreach(builder.readPreference)
    retryWrites.foreach(builder.retryWrites)
    retryReads.foreach(builder.retryReads)
    credential.map(_.wrapped).foreach(builder.credential)
    builder.build()
  }
}

object MongoSettings {

  val config: Config[MongoSettings] = {
    val connectionString = ConnectionString.config.nested("connectionString").optional
    val applicationName  = Config.string("applicationName").optional
    val readConcern      = ReadConcern.config.nested("readConcern").optional
    val writeConcern     = WriteConcern.config.nested("writeConcern").optional
    val readPreference   = ReadPreference.config.nested("readPreference").optional
    val retryWrites      = Config.boolean("retryWrites").optional
    val retryReads       = Config.boolean("retryReads").optional
    val credential       = Credential.config.nested("credential").optional

    (connectionString zip
      applicationName zip
      readConcern zip
      writeConcern zip
      readPreference zip
      retryWrites zip
      retryReads zip
      credential).nested("mongodb").map((MongoSettings.apply _).tupled)

  }

}
