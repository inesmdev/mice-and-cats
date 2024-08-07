note
	description: "Main window for this application."
	author: "Generated by the Vision Application Wizard."
	date: "$Date: 2024/7/3 12:45:52 $"
	revision: "1.0.1"

class
	MAIN_WINDOW

inherit
	EV_TITLED_WINDOW
		redefine
			create_interface_objects,
			initialize,
			is_in_default_state
		end

create
	default_create

feature {NONE} -- Initialization

	create_interface_objects
			-- <Precursor>
		do
			create assets.load_from_directory ("../mice-and-cats-java/src/main/resources")
				-- Create main container.
			create main_container
			create renderer.make_with_size (Window_width, Window_height, assets)
			create drawing_area
			new_game
		end

	initialize
			-- Build the interface for this window.
		do
			Precursor {EV_TITLED_WINDOW}

			drawing_area.expose_actions.extend (agent on_draw)
			drawing_area.resize_actions.extend (agent on_resize)

			build_main_container
			main_container.extend (drawing_area)

			extend (main_container)

				-- Execute `request_close_window' when the user clicks
				-- on the cross in the title bar.
			close_request_actions.extend (agent request_close_window)

			key_press_actions.extend (agent on_key_press)

				-- Set the title of the window.
			set_title (Window_title)

				-- Set the initial size of the window.
			set_size (Window_width, Window_height)
		end

	is_in_default_state: BOOLEAN
			-- Is the window in its default state?
			-- (as stated in `initialize')
		do
			Result := (width = Window_width) and then
				(height = Window_height) and then
				(title.is_equal (Window_title))
		end

feature {NONE} -- Implementation, Close event

	request_close_window
			-- Process user request to close the window.
		local
			question_dialog: EV_CONFIRMATION_DIALOG
		do
			create question_dialog.make_with_text (Label_confirm_close_window)
			question_dialog.show_modal_to_window (Current)

			if question_dialog.selected_button ~ (create {EV_DIALOG_CONSTANTS}).ev_ok then
					-- Destroy the window.
				destroy

					-- End the application.
					--| TODO: Remove next instruction if you don't want the application
					--|       to end when the first window is closed..
				if attached (create {EV_ENVIRONMENT}).application as a then
					a.destroy
				end
			end
		end

feature {NONE} -- Game state and timing

	last_update: TIME
			-- The timestamp of the last simulation tick

	update_seconds: REAL = 0.1
			-- the update inteval

	game_state: CHECKERBOARD
			-- the current game state

	new_game
			-- creates and starts a new game with new subways and new entitites
		local
			settings: SETTINGS
		do
			create settings
			create game_state.make (settings)
			create last_update.make_now
		end

	maybe_do_game_tick
			-- updates the game if a certain amount of time has elapsed since the last update
		local
			now: TIME
		do
			create now.make_now

			if now.relative_duration (last_update).fine_seconds_count >= update_seconds then
				game_state.do_update
				last_update.fine_second_add (update_seconds)
			end

		end

feature {NONE} -- Drawing Area and Pixmap

	drawing_area: EV_DRAWING_AREA
			-- Area for custom drawing.

	renderer: RENDERER
			-- Used to draw to an off-screen pixmap for double buffering.

	assets: ASSETS
			-- drawable assets

	on_resize (x, y, w, h: INTEGER)
			-- notify the renderer about the change in resolution
		do
			renderer.resize (w, h)
		end

	on_draw (x, y, w, h: INTEGER)
			-- Handle paint event.
		require
			wide_enough: renderer.width = drawing_area.width
			tall_enough: renderer.height = drawing_area.height
		do
			maybe_do_game_tick

			game_state.draw (renderer, super_vision)

			drawing_area.draw_pixmap (0, 0, renderer.pixmap)
			drawing_area.redraw -- immediately request next repaint
		end

feature -- Event handling

	super_vision: BOOLEAN
			-- whether to show things that should normally be invisible

	on_key_press (key: EV_KEY)
			-- Handle key press events.
		do

			if key.code = key.Key_a or key.code = key.Key_left then
				game_state.move_player (0)
			elseif key.code = key.Key_w or key.code = key.Key_up then
				game_state.move_player (1)
			elseif key.code = key.Key_s or key.code = key.Key_down then
				game_state.move_player (2)
			elseif key.code = key.Key_d or key.code = key.Key_right then
				game_state.move_player (3)
			elseif key.code = key.Key_r then
				new_game
			elseif key.code = key.Key_v then
				super_vision := not super_vision
			end

		end

feature {NONE} -- Implementation

	main_container: EV_VERTICAL_BOX
			-- Main container (contains all widgets displayed in this window).

	build_main_container
			-- Populate `main_container'.
		do
				-- main_container.extend (create {EV_TEXT}) -- Remove this line since it's replaced by drawing area
		ensure
			main_container_created: main_container /= Void
		end

feature {NONE} -- Implementation / Constants

	Window_title: STRING = "mice_and_cats_eiffel_gui"
			-- Title of the window.

	Window_width: INTEGER = 800
			-- Initial width for this window.

	Window_height: INTEGER = 600
			-- Initial height for this window.

	Label_confirm_close_window: STRING = "You are about to close this window.%NClick OK to proceed."
			-- String for the confirmation dialog box that appears
			-- when the user try to close the first window.

end
