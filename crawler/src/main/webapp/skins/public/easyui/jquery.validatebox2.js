; (function($) {
    function init(e) {
        $(e).addClass("validatebox-text")
    }
    function _3cb(e) {
        var t = $.data(e, "validatebox");
        t.validating = !1,
        $(e).tooltip("destroy"),
        $(e).unbind(),
        $(e).remove()
    }
    function _3ce(e) {
        var t = $(e),
        n = $.data(e, "validatebox");
        t.unbind(".validatebox").bind("focus.validatebox",
        function() {
            n.validating = !0,
            n.value = undefined,
            function() {
                n.validating && (n.value != t.val() ? (n.value = t.val(), n.timer && clearTimeout(n.timer), n.timer = setTimeout(function() {
                    $(e).validatebox("validate")
                },
                n.options.delay)) : _3d5(e), setTimeout(arguments.callee, 200))
            } ()
        }).bind("blur.validatebox",
        function() {
            n.timer && (clearTimeout(n.timer), n.timer = undefined),
            n.validating = !1,
            _3d1(e)
        }).bind("mouseenter.validatebox",
        function() {
            t.hasClass("validatebox-invalid") && _3d2(e)
        }).bind("mouseleave.validatebox",
        function() {
            n.validating || _3d1(e)
        })
    }
    function _3d2(e) {
        var t = $.data(e, "validatebox"),
        n = t.options;
        $(e).tooltip($.extend({},
        n.tipOptions, {
            content: t.message,
            position: n.tipPosition,
            deltaX: n.deltaX
        })).tooltip("show"),
        t.tip = !0
    }
    function _3d5(e) {
        var t = $.data(e, "validatebox");
        t && t.tip && $(e).tooltip("reposition")
    }
    function _3d1(e) {
        var t = $.data(e, "validatebox");
        t.tip = !1,
        $(e).tooltip("hide");
    }
    function _3da(_3db) {
        function _3de(e) {
            _3dc.message = e
        }
        function _3df(_3e0) {
            var _3e1 = /([a-zA-Z_]+)(.*)/.exec(_3e0),
            rule = opts.rules[_3e1[1]];
            if (rule && _3dd) {
                var _3e2 = eval(_3e1[2]);
                if (!rule.validator(_3dd, _3e2)) {
                    box.addClass("validatebox-invalid");
                    var _3e3 = rule.message;
                    if (_3e2) {
                        for (var i = 0; i < _3e2.length; i++) {
                            _3e3 = _3e3.replace(new RegExp("\\{" + i + "\\}", "g"), _3e2[i])
                        }
                    }
                    return _3de(opts.invalidMessage || _3e3),
                    _3dc.validating && _3d2(_3db),
                    !1
                }
            }
            return ! 0
        }
        var _3dc = $.data(_3db, "validatebox"),
        opts = _3dc.options,
        box = $(_3db),
        _3dd = box.val();
        if (opts.required && _3dd == "") {
            return box.addClass("validatebox-invalid"),
            _3de(opts.missingMessage),
            _3dc.validating && _3d2(_3db),
            !1
        }
        if (opts.validType) {
            if (typeof opts.validType == "string") {
                if (!_3df(opts.validType)) {
                    return ! 1
                }
            } else {
                for (var i = 0; i < opts.validType.length; i++) {
                    if (!_3df(opts.validType[i])) {
                        return ! 1
                    }
                }
            }
        }
        return box.removeClass("validatebox-invalid"),
        _3d1(_3db),
        !0
    }
    $.fn.validatebox = function(e, t) {
        return typeof e == "string" ? $.fn.validatebox.methods[e](this, t) : (e = e || {},
        this.each(function() {
            var t = $.data(this, "validatebox");
            t ? $.extend(t.options, e) : (init(this), $.data(this, "validatebox", {
                options: $.extend({},
                $.fn.validatebox.defaults, $.fn.validatebox.parseOptions(this), e)
            })),
            _3ce(this)
        }))
    },
    $.fn.validatebox.methods = {
        options: function(e) {
            return $.data(e[0], "validatebox").options
        },
        destroy: function(e) {
            return e.each(function() {
                _3cb(this)
            })
        },
        validate: function(e) {
            return e.each(function() {
                _3da(this)
            })
        },
        isValid: function(e) {
            return _3da(e[0])
        }
    },
    $.fn.validatebox.parseOptions = function(e) {
        var t = $(e);
        return $.extend({},
        $.parser.parseOptions(e, ["validType", "missingMessage", "invalidMessage", "tipPosition", {
            delay: "number",
            deltaX: "number"
        }]), {
            required: t.attr("required") ? !0 : undefined
        })
    },
    $.fn.validatebox.defaults = {
        required: !1,
        validType: null,
        delay: 200,
        missingMessage: "\u6B64\u9879\u5FC5\u586B.",
        invalidMessage: null,
        tipPosition: "bottom",
        deltaX: 0,
        tipOptions: {
            showEvent: "none",
            hideEvent: "none",
            showDelay: 0,
            hideDelay: 0,
            zIndex: "",
            onShow: function() {
                $(this).tooltip("tip").css({
                    color: "#000",
                    borderColor: "#CC9933",
                    backgroundColor: "#FFFFCC"
                })
            },
            onHide: function() {
                $(this).tooltip("destroy")
            }
        },
        rules: {
            email: {
                validator: function(e) {
                    return /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test(e)
                },
                message: "\u8BF7\u8F93\u5165\u4E00\u4E2A\u6709\u6548\u7684\u90AE\u7BB1."
            },
            url: {
                validator: function(e) {
                    return /^(https?|ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(\#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i.test(e)
                },
                message: "\u8BF7\u8F93\u5165\u4E00\u4E2A\u6709\u6548\u7684\u7F51\u5740."
            },
            length: {
                validator: function(e, t) {alert("ddd");
                    var n = $.trim(e).length;
                    return n >= t[0] && n <= t[1]
                },
                message: "\u8BF7\u8F93\u5165\u957F\u5EA6\u5927\u4E8E{0}\u5C0F\u4E8E{1}\u7684\u5B57\u7B26."
            },
            remote: {
                validator: function(e, t) {
                    var n = {};
                    n[t[1]] = e;
                    var r = $.ajax({
                        url: t[0],
                        dataType: "json",
                        data: n,
                        async: !1,
                        cache: !1,
                        type: "post"
                    }).responseText;
                    return r == "true"
                },
                message: "\u8BF7\u56FA\u5B9A\u6B64\u5B57\u6BB5."
            }
        }
    }
})(jQuery);; (function(e) {
    function t(t, n) {
        function p() {
            o.unbind();
            var t = e("#" + s).contents().find("body"),
            r = t.html();
            if (r == "") {
                if (--h) {
                    setTimeout(p, 100);
                    return
                }
                return
            }
            var i = t.find(">textarea");
            if (i.length) r = i.val();
            else {
                var u = t.find(">pre");
                u.length && (r = u.html())
            }
            n.success && n.success(r),
            setTimeout(function() {
                o.unbind(),
                o.remove()
            },
            100)
        }
        n = n || {};
        var r = {};
        if (n.onSubmit && n.onSubmit.call(t, r) == 0) return;
        var i = e(t);
        n.url && i.attr("action", n.url);
        var s = "easyui_frame_" + (new Date).getTime(),
        o = e("<iframe id=" + s + " name=" + s + "></iframe>").attr("src", window.ActiveXObject ? "javascript:false": "about:blank").css({
            position: "absolute",
            top: -1e3,
            left: -1e3
        }),
        u = i.attr("target"),
        a = i.attr("action");
        i.attr("target", s);
        var f = e();
        try {
            o.appendTo("body"),
            o.bind("load", p);
            for (var l in r) {
                var c = e('<input type="hidden" name="' + l + '">').val(r[l]).appendTo(i);
                f = f.add(c)
            }
            i[0].submit()
        } finally {
            i.attr("action", a),
            u ? i.attr("target", u) : i.removeAttr("target"),
            f.remove()
        }
        var h = 10
    }
    function n(t, n) {
        function s(n) {
            var i = e(t);
            for (var s in n) {
                var f = n[s],
                l = u(s, f);
                if (!l.length) {
                    var c = i.find('input[numberboxName="' + s + '"]');
                    c.length ? c.numberbox("setValue", f) : (e('input[name="' + s + '"]', i).val(f), e('textarea[name="' + s + '"]', i).val(f), e('select[name="' + s + '"]', i).val(f))
                }
                a(s, f)
            }
            r.onLoadSuccess.call(t, n),
            o(t)
        }
        function u(n, r) {
            var i = e(t).find('input[name="' + n + '"][type=radio], input[name="' + n + '"][type=checkbox]');
            return i._propAttr("checked", !1),
            i.each(function() {
                var t = e(this); (t.val() == String(r) || e.inArray(t.val(), r) >= 0) && t._propAttr("checked", !0)
            }),
            i
        }
        function a(n, r) {
            var i = e(t),
            s = ["combobox", "combotree", "combogrid", "datetimebox", "datebox", "combo"],
            o = i.find('[comboName="' + n + '"]');
            if (o.length) for (var u = 0; u < s.length; u++) {
                var a = s[u];
                if (o.hasClass(a + "-f")) {
                    o[a]("options").multiple ? o[a]("setValues", r) : o[a]("setValue", r);
                    return
                }
            }
        }
        e.data(t, "form") || e.data(t, "form", {
            options: e.extend({},
            e.fn.form.defaults)
        });
        var r = e.data(t, "form").options;
        if (typeof n == "string") {
            var i = {};
            if (r.onBeforeLoad.call(t, i) == 0) return;
            e.ajax({
                url: n,
                data: i,
                dataType: "json",
                success: function(e) {
                    s(e)
                },
                error: function() {
                    r.onLoadError.apply(t, arguments)
                }
            })
        } else s(n)
    }
    function r(t) {
        e("input,select,textarea", t).each(function() {
            var t = this.type,
            n = this.tagName.toLowerCase();
            if (t == "text" || t == "hidden" || t == "password" || n == "textarea") this.value = "";
            else if (t == "file") {
                var r = e(this);
                r.after(r.clone().val("")),
                r.remove()
            } else t == "checkbox" || t == "radio" ? this.checked = !1 : n == "select" && (this.selectedIndex = -1)
        }),
        e.fn.combo && e(".combo-f", t).combo("clear"),
        e.fn.combobox && e(".combobox-f", t).combobox("clear"),
        e.fn.combotree && e(".combotree-f", t).combotree("clear"),
        e.fn.combogrid && e(".combogrid-f", t).combogrid("clear"),
        o(t)
    }
    function i(t) {
        t.reset();
        var n = e(t);
        e.fn.combo && n.find(".combo-f").combo("reset"),
        e.fn.combobox && n.find(".combobox-f").combobox("reset"),
        e.fn.combotree && n.find(".combotree-f").combotree("reset"),
        e.fn.combogrid && n.find(".combogrid-f").combogrid("reset"),
        e.fn.spinner && n.find(".spinner-f").spinner("reset"),
        e.fn.timespinner && n.find(".timespinner-f").timespinner("reset"),
        e.fn.numberbox && n.find(".numberbox-f").numberbox("reset"),
        e.fn.numberspinner && n.find(".numberspinner-f").numberspinner("reset"),
        o(t)
    }
    function s(n) {
        var r = e.data(n, "form").options,
        i = e(n);
        i.unbind(".form").bind("submit.form",
        function() {
            return setTimeout(function() {
                t(n, r)
            },
            0),
            !1
        })
    }
    function o(t) {
        if (e.fn.validatebox) {
            var n = e(t);
            n.find(".validatebox-text:not(:disabled)").validatebox("validate");
            var r = n.find(".validatebox-invalid");
            return r.filter(":not(:disabled):first").focus(),
            r.length == 0
        }
        return ! 0
    }
    e.fn.form = function(t, n) {
        return typeof t == "string" ? e.fn.form.methods[t](this, n) : (t = t || {},
        this.each(function() {
            e.data(this, "form") || e.data(this, "form", {
                options: e.extend({},
                e.fn.form.defaults, t)
            }),
            s(this)
        }))
    },
    e.fn.form.methods = {
        submit: function(n, r) {
            return n.each(function() {
                t(this, e.extend({},
                e.fn.form.defaults, r || {}))
            })
        },
        load: function(e, t) {
            return e.each(function() {
                n(this, t)
            })
        },
        clear: function(e) {
            return e.each(function() {
                r(this)
            })
        },
        reset: function(e) {
            return e.each(function() {
                i(this)
            })
        },
        validate: function(e) {
            return o(e[0])
        }
    },
    e.fn.form.defaults = {
        url: null,
        onSubmit: function(t) {
            return e(this).form("validate")
        },
        success: function(e) {},
        onBeforeLoad: function(e) {},
        onLoadSuccess: function(e) {},
        onLoadError: function() {}
    }
})(jQuery)