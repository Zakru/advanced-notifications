package com.github.zakru.advancednotifications.condition;

import com.github.zakru.advancednotifications.notification.InventoryComparator;

public interface InventoryChecker
{
	public static class Pointer
	{
		public InventoryChecker object;

		public Pointer(InventoryChecker object)
		{
			this.object = object;
		}
	}

	boolean isFulfilled(int count, int param);
	boolean takesParam();

	InventoryChecker EQUAL = new InventoryChecker()
	{
		@Override
		public boolean isFulfilled(int count, int param)
		{
			return count == 0;
		}

		@Override
		public boolean takesParam()
		{
			return true;
		}

		@Override
		public String toString()
		{
			return "=";
		}
	};

	InventoryChecker LESS_THAN = new InventoryChecker()
	{
		@Override
		public boolean isFulfilled(int count, int param)
		{
			return count < 0;
		}

		@Override
		public boolean takesParam()
		{
			return true;
		}

		@Override
		public String toString()
		{
			return "<";
		}
	};

	InventoryChecker GREATER_THAN = new InventoryChecker()
	{
		@Override
		public boolean isFulfilled(int count, int param)
		{
			return count > 0;
		}

		@Override
		public boolean takesParam()
		{
			return true;
		}

		@Override
		public String toString()
		{
			return ">";
		}
	};

	InventoryChecker LESS_OR_EQUAL = new InventoryChecker()
	{
		@Override
		public boolean isFulfilled(int count, int param)
		{
			return count <= 0;
		}

		@Override
		public boolean takesParam()
		{
			return true;
		}

		@Override
		public String toString()
		{
			return "≤";
		}
	};

	InventoryChecker GREATER_OR_EQUAL = new InventoryChecker()
	{
		@Override
		public boolean isFulfilled(int count, int param)
		{
			return count >= 0;
		}

		@Override
		public boolean takesParam()
		{
			return true;
		}

		@Override
		public String toString()
		{
			return "≥";
		}
	};

	InventoryChecker[] CHECKERS = new InventoryChecker[]
	{
		EQUAL, LESS_THAN, GREATER_THAN, LESS_OR_EQUAL, GREATER_OR_EQUAL,
	};
}
