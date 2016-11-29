(function($) {
	/**
	 * 检查menu内是否有可显示的元素
	 */
	var checkDisplay = function(jq) {
		var hasDisplay = false;
		jq.children(".menu-item").each(function() {
			if ($(this).css("display") != "none") {
				hasDisplay = true;
				return false;
			}
		});
		return hasDisplay;
	};

	/**
	 * 修改原来的显示方法，没有可显示元素则不显示menu
	 */
	$.extend($.fn.menu.methods, {
		oldShow : $.fn.menu.methods.show,
		show : function(jq, pos, useOldShow) {
			var that  = this;
			if (useOldShow) {
				return that.oldShow(jq, pos);
			} else {
				return jq.each(function() {
					if (checkDisplay($(this))) {
						that.oldShow($(this), pos);
					}
				});
			}
		}
	});

	/**
	 * 删除子菜单的方法
	 */
	function removeSubMenu(target) {
		if (target && target.submenu) {
			// 删除子菜单
			var submenu = target.submenu;
			submenu.remove();
			target.submenu = null;
			
			// 删除右箭头的图标
			$(target).children("div.menu-rightarrow").remove();
		}
	}

	/**
	 * 增加删除子菜单的方法
	 */
	$.extend($.fn.menu.methods, {
		destroySubMenu : function(jq, target) {
			return target.each(function() {
				removeSubMenu(this);
			});
		}
	});
})(jQuery);