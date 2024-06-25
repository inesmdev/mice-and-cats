note
	description: "cli-cat-mouse application root class"
	date: "$Date$"
	revision: "$Revision$"

class
	APPLICATION

inherit
	ARGUMENTS_32

create
	make

feature {NONE} -- Initialization

    make
            -- Initialize the main application.
        local
            checkerboard: CHECKERBOARD
        do
            create checkerboard.make
            checkerboard.print_grid
        end

end
