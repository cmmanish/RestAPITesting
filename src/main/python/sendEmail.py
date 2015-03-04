#!/usr/bin/python

import smtplib

fromaddr = 'naanu.nane@gmail.com'
toaddrs = 'cmmanish@gmail.com'
gmail_user = 'naanu.nane@gmail.com'
gmail_pwd = 'hsinam123'

msg = '"This is a test mail ... :-)'

# Sending the mail

server = smtplib.SMTP('smtp.gmail.com:587')
server.starttls()
server.login(gmail_user, gmail_pwd)
server.sendmail(fromaddr, toaddrs, msg)
server.quit()