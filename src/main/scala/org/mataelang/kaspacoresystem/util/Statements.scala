package org.mataelang.kaspacoresystem.util

object Statements {
  def push_event(ts: String, company:String, device_id: String, year: Integer, month: Integer, day: Integer,
                               hour: Integer, minute: Integer, second: Integer, milisecond: Long, protocol: String, ip_type: String, src_mac: String,
                               dest_mac: String, src_ip: String, dest_ip: String, src_port: Integer, dest_port: Integer,
                               alert_msg: String, classification: Integer, priority: Integer, sig_id: Integer,
                               sig_gen: Integer, sig_rev: Integer, src_country: String, src_region: String, src_city: String, dest_country: String,
                               dest_region: String, dest_city: String ): String =
    s"""
       |INSERT INTO public.events ("ts", "company", "device_id", "year", "month", "day", "hour", "minute", "second", "milisecond",
       |"protocol", "ip_type", "src_mac", "dest_mac", "src_ip", "dest_ip", "src_port", "dest_port",
       |"alert_msg", "classification", "priority", "sig_id", "sig_gen", "sig_rev", "src_country", "src_region", "src_city", "dest_country", "dest_region", "dest_city")
       |values('$ts', '$company', '$device_id', $year, $month, $day, $hour, $minute, $second, $milisecond, '$protocol',
       |'$ip_type', '$src_mac', '$dest_mac', '$src_ip', '$dest_ip',$src_port, $dest_port, '$alert_msg',
       |$classification, $priority, $sig_id, $sig_gen, $sig_rev, '$src_country', '$src_region', '$src_city', '$dest_country', '$dest_region', '$dest_city')""".stripMargin
}
