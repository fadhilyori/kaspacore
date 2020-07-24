package org.mataelang.kaspacoresystem.util

import org.mataelang.kaspacoresystem.jobs.DataStream.getPostgreSQLSession
import org.mataelang.kaspacoresystem.models.EventObj

object SQLHelper {

  def pushEvent(value: EventObj): Unit = {
    // PostgreSQL Connection
    val conn = getPostgreSQLSession
//    conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)

    try {

      val queryStatement = conn.prepareStatement(Statements.push_event(value.ts, value.company, value.device_id, value.year, value.month, value.day, value.hour,
        value.minute, value.second, value.milisecond, value.protocol, value.ip_type, value.src_mac, value.dest_mac,
        value.src_ip, value.dest_ip, value.src_port, value.dest_port, value.alert_msg, value.classification,
        value.priority, value.sig_id, value.sig_gen, value.sig_rev, value.src_country, value.src_region, value.src_city, value.dest_country, value.dest_region, value.dest_city))

      queryStatement.executeUpdate()

      println("Data sent : " + value.year + "/" + value.month + "/" + value.day + " " + value.hour + ":" + value.minute + ":" + value.second + ":" + value.milisecond)
    } finally {
      conn.close()
    }
  }

}
