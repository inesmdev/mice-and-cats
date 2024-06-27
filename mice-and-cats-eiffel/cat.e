note
	description: "Summary description for {CAT}."
	author: ""
	date: "$Date$"
	revision: "$Revision$"

class
	CAT

inherit

	PLAYER
		rename
			make as p_make
		end

create
	make

feature {NONE} -- Initialization

	make (a_name: STRING)
		require
			a_name_not_void: a_name /= VOID
		do
			p_make
			set_name(a_name)
		end

end
