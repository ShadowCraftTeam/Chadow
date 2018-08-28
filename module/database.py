#!/usr/bin/python3
# -*- coding: utf-8 -*-

import pymysql
import getpass
import traceback


# The MIT License (MIT)
# Copyright (C) 2018 ShadowCreative

# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.
class MysqlBase:
    """
    MysqlBase can indirectly connect to Mysql server to send and receive data.
    """

    def __init__(self, user, password, host, port):
        self.user = user
        self.password = password
        self.host = host
        self.port = port
        self._connect = None
        self._cur = None
        self._pExecutor = None

    def __initialize__(self, charset):
        if self._connect is not None:
            raise pymysql.OperationalError("Already initialized")

        try:
            self._connect = pymysql.connect(host=self.host, user=self.user, passwd=self.password, port=self.port,
                                            charset=charset)
            if self._connect is not None:
                self._cur = self._connect.cursor(pymysql.cursors.DictCursor)
            return True
        except pymysql.Error:
            traceback.print_exc()
            return False

    def connect(self, character_set="utf8", debug=True):
        if self.password is None:
            self.password = getpass.getpass("Please input password {U}@{H}".format(U=self.user, H=self.host))

        if self.__initialize__(character_set):
            if debug:
                print("Connected the MySQL Database {U}@{H}".format(U=self.user, H=self.host))
        else:
            raise pymysql.DatabaseError('Not connected the database, Please check your information from param')

        try:
            self._connect = pymysql.connect(host=self.host, user=self.user, passwd=self.password, port=self.port,
                                            charset=character_set)
        except pymysql.Error as _:
            pass
